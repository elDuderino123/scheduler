package com.system.management.JobScheduler.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.system.management.JobScheduler.commons.Constants;
import com.system.management.JobScheduler.db.entity.JobStatus;
import com.system.management.JobScheduler.db.repository.JobStatusRepo;
import com.system.management.JobScheduler.job.TaskExecutor;
import com.system.management.JobScheduler.model.execution.PipelineExecDetail;
import com.system.management.JobScheduler.model.execution.ServerInfo;
import com.system.management.JobScheduler.model.execution.StageExecDetail;
import com.system.management.JobScheduler.model.registration.JobExecutionDetails;
import com.system.management.JobScheduler.model.registration.JobRegistrationDto;

@Service
public class StageExecutorService {

	private static Logger LOGGER = LoggerFactory.getLogger(TaskExecutor.class);

	@Autowired
	private MailService mailSender;

	@Autowired
	private JschExecutorService jschExecutor;

	@Autowired
	private JobStatusRepo jobStatusRepo;

	@Autowired
	private CacheService cacheService;

	@Autowired
	private LockingService lockingService;

	public void executeStage(JobRegistrationDto dto, Date startTime, UUID jobLaunchId, String pipelineName) {

		final JobExecutionDetails jobExecDetails = dto.getJobExceutionDetails();
		dto.getServers().forEach(serverDetails -> {

			String host = serverDetails.getHost();
			int port = serverDetails.getPort();
			String user = serverDetails.getUser();
			String password = serverDetails.getPassword();

			StringBuilder command = new StringBuilder();

			command.append(jobExecDetails.getBashCommand()).append(" ");
			command.append(dto.getJobName()).append(" "); // arg 1 - Job Name
			command.append(Constants.PIPELINE_JOB).append(" "); // arg 2 - Job Type
			command.append(jobLaunchId.toString()).append(" "); // arg 3 - Launch Id
			command.append(host).append(" "); // arg 4 - Server

			jobExecDetails.getArguments().forEach(arg -> {
				command.append(arg).append(" ");
			});

			try {
				// Submit command for execution
				String response = jschExecutor.sendCommand(command.toString(), host, port, user, password);
				LOGGER.info("Job " + dto.getJobName() + " successfully submitted \n Command output: " + response);

				// Make an entry in cache if not already there ;
				makeCacheEntry(jobLaunchId, dto, host, startTime);

				// Make the status entry in DB
				JobStatus status = jobStatusRepo.findById(jobLaunchId).get();
				String remark = status.getRemark();
				remark = remark + dto.getJobName() + " , ";

			} catch (Exception e) {
				LOGGER.error("Exceptin while executing command ", e);
				String subject = "Execution of job: " + dto.getJobName() + " failed for host: " + host;
				String content = subject + "\n" + ExceptionUtils.getStackTrace(e);
				mailSender.sendMail(dto.getEmailTo(), subject, content);
			}

		});
	}

	public void makeCacheEntry(UUID jobLaunchId, JobRegistrationDto dto, String host, Date startTime)
			throws JsonParseException, JsonMappingException, IOException {

		try {
			// Obtain the lock
			lockingService.getLockForLuanchId(jobLaunchId);

			String jobStr = cacheService.getForUUID(jobLaunchId);
			ObjectMapper mapper = new ObjectMapper();

			PipelineExecDetail pipeLineExecDeatils = null;
			StageExecDetail stageDetails = null;
			if (jobStr != null) {
				pipeLineExecDeatils = mapper.readValue(jobStr, PipelineExecDetail.class);
				stageDetails = pipeLineExecDeatils.getJobSequence().stream().filter(stage -> {
					return stage.getName().equals(dto.getJobName());
				}).findFirst().get();

				stageDetails.setStartTime(startTime.getTime());

				List<ServerInfo> servers = stageDetails.getHosts();
				if (servers == null) {
					servers = new ArrayList<>();
				}
				ServerInfo sInfo = new ServerInfo();
				sInfo.setStartTime(new Date().getTime());
				sInfo.setServerName(host);
				servers.add(sInfo);
			}

			jobStr = mapper.writeValueAsString(stageDetails);
			cacheService.setForUUID(jobLaunchId, jobStr);
		} finally {
			// Release lock for UUID
			lockingService.releaseLockForLaunchId(jobLaunchId);

		}

	}

}
