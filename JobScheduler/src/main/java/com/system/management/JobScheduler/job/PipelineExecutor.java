package com.system.management.JobScheduler.job;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.system.management.JobScheduler.commons.Constants;
import com.system.management.JobScheduler.db.entity.JobStatus;
import com.system.management.JobScheduler.db.repository.JobStatusRepo;
import com.system.management.JobScheduler.model.execution.PipelineExecDetail;
import com.system.management.JobScheduler.model.execution.StageExecDetail;
import com.system.management.JobScheduler.model.registration.JobRegistrationDto;
import com.system.management.JobScheduler.model.registration.PipelineRegistrationDto;
import com.system.management.JobScheduler.model.registration.StageSequenceDetail;
import com.system.management.JobScheduler.services.CacheService;
import com.system.management.JobScheduler.services.JobDetailsService;
import com.system.management.JobScheduler.services.MailService;
import com.system.management.JobScheduler.services.PipelineDetailsService;
import com.system.management.JobScheduler.services.StageExecutorService;

@Service
public class PipelineExecutor extends QuartzJobBean {

	private static Logger LOGGER = LoggerFactory.getLogger(PipelineExecutor.class);
	
	@Autowired
	private MailService mailSender;
	
	@Autowired
	private JobStatusRepo jobStatusRepo;
	
	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private JobDetailsService jobDetailsService;
	
	@Autowired
	private PipelineDetailsService pipelineDetailsService;
	
	@Autowired
	private StageExecutorService stageExecutorService;
	

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getMergedJobDataMap();

		String pipelineName = jobDataMap.getString("pipelineName");
		
		PipelineRegistrationDto dto = pipelineDetailsService.getPipelineDetails(pipelineName);
		final UUID jobLaunchId = Generators.timeBasedGenerator().generate();
		final Date startTime = new Date();

		JobStatus jobStatus = new JobStatus();
		jobStatus.setJobLaunchId(jobLaunchId);
		jobStatus.setStartTime(new Date());
		jobStatus.setStatus(Constants.JOB_STARTED);
		jobStatus.setType(Constants.PIPELINE_JOB);
		jobStatus.setName(dto.getPipelineName());

		// Make DB Entry
		jobStatusRepo.save(jobStatus);
		
		StageSequenceDetail startStage = null;
		ObjectMapper mapper = new ObjectMapper();
		
		//Make the place holders for all the stages in memory 
		PipelineExecDetail pipelineExecDetails = new PipelineExecDetail();
		pipelineExecDetails.setName(pipelineName);
		pipelineExecDetails.setJobSequence(new LinkedList<>());
		
		
		dto.getSequence().forEach(stageSeqDetail ->{
			StageExecDetail stageDetail = new StageExecDetail();
			stageDetail.setName(stageSeqDetail.getStageName());
			stageDetail.setExecutionOrder(stageSeqDetail.getExecutionSequence());
			stageDetail.setHosts(new ArrayList<>());
			pipelineExecDetails.getJobSequence().add(stageDetail);
		});
		
		
		try {
			String pipelineDetails = mapper.writeValueAsString(pipelineExecDetails);
			cacheService.setForUUID(jobLaunchId, pipelineDetails);
			
		} catch (JsonProcessingException e) {
			LOGGER.error("Exception while creatig In-Memory entry for pipeline: "+pipelineName);
			
			String subject = "Execution of pipeline:  "+pipelineName + " failed";
			String content = subject + System.lineSeparator() + ExceptionUtils.getStackTrace(e);
			mailSender.sendMail(dto.getToEmail(), subject, content);
		} finally {
			
		}
		
		startStage = dto.getSequence().stream().filter(stgSeq ->{
			return stgSeq.getExecutionSequence().intValue() == 1;
		}).findFirst().get();
		
		if(startStage != null) {
			JobRegistrationDto stageDetailDTO = jobDetailsService.getJobDetails(startStage.getStageName());
			stageExecutorService.executeStage(stageDetailDTO, startTime, jobLaunchId, dto.getPipelineName());
		}

	}
	

}
