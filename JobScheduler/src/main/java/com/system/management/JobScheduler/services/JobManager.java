package com.system.management.JobScheduler.services;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.management.JobScheduler.commons.CommonUtils;
import com.system.management.JobScheduler.commons.Constants;
import com.system.management.JobScheduler.db.entity.JobStatus;
import com.system.management.JobScheduler.db.entity.PipelineDetails;
import com.system.management.JobScheduler.db.repository.JobStatusRepo;
import com.system.management.JobScheduler.db.repository.PipelineDetailsRepo;
import com.system.management.JobScheduler.model.execution.PipelineExecDetail;
import com.system.management.JobScheduler.model.execution.ServerInfo;
import com.system.management.JobScheduler.model.execution.StageExecDetail;
import com.system.management.JobScheduler.model.registration.JobRegistrationDto;
import com.system.management.JobScheduler.model.registration.PipelineRegistrationDto;

@Service
public class JobManager {

	private static Logger LOGGER = LoggerFactory.getLogger(JobManager.class);

	@Autowired
	private CacheService cacheService;

	@Autowired
	private MailService mailService;

	@Autowired
	private JobDetailsService jobDetailsService;

	@Autowired
	private PipelineDetailsService pipelineDetailsService;

	@Autowired
	private StageExecutorService stageExecutorService;
	
	@Autowired
	private LockingService lockingService;
	
	@Autowired
	private Scheduler scheduler;

	@Autowired
	JobStatusRepo jobStatusRepo;
	

	public void processPipelineJobStatus(String jobName, String server, UUID jobLaunchId, int statusCode) {
		try {

			//Obtain lock to edit cache
			lockingService.getLockForLuanchId(jobLaunchId);
			
			// Check if some job in the pipeline has already been failed
			JobStatus jobStatus = jobStatusRepo.findById(jobLaunchId).get();

			PipelineRegistrationDto pDto = pipelineDetailsService.getPipelineDetails(jobStatus.getName());
			JobRegistrationDto dto = jobDetailsService.getJobDetails(jobName);

			if (!jobStatus.getStatus().equals(Constants.JOB_FAILED)) {
				return;
			}

			// Check if status code is error
			if (statusCode > 0) {
				String subject = "Excution of Job: " + jobName + " failed ";
				String content = CommonUtils.generateMailContentForFailure(jobName, server, statusCode);

				// Mark the status as fail in DB
				setJobStatusInDb(jobLaunchId, jobName, true, statusCode);

				// Remove the cache entry for the job
				cacheService.removeFromCache(jobLaunchId);

				// Mail
				mailService.sendMail(dto.getEmailTo(), subject, content);

				return;

			}

			
			// Fetch job from cache
			String jobStr = cacheService.getForUUID(jobLaunchId);
			ObjectMapper mapper = new ObjectMapper();

			PipelineExecDetail pipelineExecDetails = mapper.readValue(jobStr, PipelineExecDetail.class);

			StageExecDetail stageExecDetails = pipelineExecDetails.getJobSequence().stream().filter(seq -> {
				return seq.getName().equals(jobName);
			}).findFirst().get();
			ServerInfo serverInfo = stageExecDetails.getHosts().stream().filter(sInfo -> {
				return sInfo.getServerName().equals(server);
			}).findFirst().get();

			serverInfo.setEndTime(new Date().getTime());

			jobStr = mapper.writeValueAsString(pipelineExecDetails);

			cacheService.setForUUID(jobLaunchId, jobStr);

			
			// Check if this was the last server for the Job
			boolean allServersFinsihed = stageExecDetails.getHosts().stream().filter(sInfo -> {
				return sInfo.getEndTime() == null;
			}).collect(Collectors.toList()).isEmpty();

			if (allServersFinsihed) {
				// Check if next job exist
				if (pipelineExecDetails.getJobSequence().size() > stageExecDetails.getExecutionOrder()) {

					// Start the next job
					stageExecutorService.executeStage(dto, new Date(), jobLaunchId, pipelineExecDetails.getName());

				} else {

					//This was the last step in pipeline mark success in DB
					setJobStatusInDb(jobLaunchId, jobName, false, statusCode);
					
					// Send success mail
					String subject = "Execution of Pipeline: " + pDto.getPipelineName() + " successful ";
					mailService.sendMail(pDto.getToEmail(), subject, subject);
				}

			} else {

				// Do nothing as next server job has already been started
			}

			//Release lock 
			lockingService.releaseLockForLaunchId(jobLaunchId);
			
		} catch (Exception e) {
			LOGGER.error("Exception while processing the job status");
		} finally {
			//Release lock 
			lockingService.releaseLockForLaunchId(jobLaunchId);
		}
	}

	public void processStandaloneJobStatus(String jobName, String server, UUID jobLaunchId, int statusCode) {

		try {
			
			//Obtain lock to edit cache
			lockingService.getLockForLuanchId(jobLaunchId);
			
			// Check if some job in the pipeline has already been failed
			JobStatus jobStatus = jobStatusRepo.findById(jobLaunchId).get();
			JobRegistrationDto jDto = jobDetailsService.getJobDetails(jobName);

			if (!jobStatus.getStatus().equals(Constants.JOB_FAILED)) {
				return;
			}

			// Check if status code is error
			if (statusCode > 0) {
				String subject = "Excution of Job: " + jobName + " failed ";
				String content = CommonUtils.generateMailContentForFailure(jobName, server, statusCode);

				// Mark the status as fail in DB
				setJobStatusInDb(jobLaunchId, jobName, true, statusCode);

				// Remove the cache entry for the job
				cacheService.removeFromCache(jobLaunchId);

				// Mail
				mailService.sendMail(jDto.getEmailTo(), subject, content);

				return;

			}

			String jobStr = cacheService.getForUUID(jobLaunchId);
			ObjectMapper mapper = new ObjectMapper();

			StageExecDetail stageExecDetails = mapper.readValue(jobStr, StageExecDetail.class);

			ServerInfo serverInfo = stageExecDetails.getHosts().stream().filter(sInfo -> {
				return sInfo.getServerName().equals(server);
			}).findFirst().get();

			serverInfo.setEndTime(new Date().getTime());

			jobStr = mapper.writeValueAsString(stageExecDetails);

			cacheService.setForUUID(jobLaunchId, jobStr);

			// Check if this was the last server for the Job
			boolean allServersFinsihed = stageExecDetails.getHosts().stream().filter(sInfo -> {
				return sInfo.getEndTime() == null;
			}).collect(Collectors.toList()).isEmpty();

			if (allServersFinsihed) {

				// Send success mail
				String subject = "Execution of Job: " + jDto.getJobName() + " successful ";
				mailService.sendMail(jDto.getEmailTo(), subject, subject);

			} else {

				// Do nothing as next server job has already been started
			}
		} catch (Exception e) {
			LOGGER.error("Eception while processing the status update for job: " + jobName, e);
		} finally {
			//Release lock 
			lockingService.releaseLockForLaunchId(jobLaunchId);
		}

	}

	public void registerPipeline(PipelineRegistrationDto dto ) {
		
		try {
			//Validate the DTO
			pipelineDetailsService.validatePipelineDetails(dto);
			
			
			//Build the job instance 
			JobDetail job = jobDetailsService.createJob(dto.getPipelineName(), true);
			
			//Build the trigger instance
			Trigger trigger = jobDetailsService.buildJobTrigger(job, dto.getCronExpression());
			
			//Persist the DTO
			pipelineDetailsService.persistPipelineDetails(dto,job.getKey().toString(),trigger.getKey().toString());
			
			//Schedule Job
			scheduler.scheduleJob(job, trigger);
			
			
		} catch(Exception e) {
			LOGGER.error("Registration for pipeline failed : "+dto.getPipelineName(),e);
			throw new RuntimeException(e);
		}
		
	}

	public void registerJob(JobRegistrationDto dto) {
		try {
			//Validate the DTO
			jobDetailsService.validateJobRegistrationDetails(dto);
			
			
			
			//Check if it standalone job
			if(dto.getIsTask().equalsIgnoreCase("true")) {
				
				//Build the job instance 
				JobDetail job = jobDetailsService.createJob(dto.getJobName(), false);
				
				//Build the trigger instance
				Trigger trigger = jobDetailsService.buildJobTrigger(job, dto.getCronExpression());
				
				//Persist the DTO
				jobDetailsService.persistJobRegDetails(dto,job.getKey().toString(),trigger.getKey().toString());
				
				
				//Schedule Job
				scheduler.scheduleJob(job, trigger);
				
				return;
				
			}
			
			//Persist the DTO
			jobDetailsService.persistJobRegDetails(dto , null,null);
			
		} catch (Exception e) {
			LOGGER.error("Validation Failed: "+dto.getJobName(),e);
			throw new RuntimeException(e);
		}
	}
	
	public void reschedulePipeline(PipelineRegistrationDto dto) {
	}
	private void setJobStatusInDb(UUID jobLaunchId, String jobName, boolean fail, int statusCode) {
		JobStatus jobStatus = jobStatusRepo.findById(jobLaunchId).get();

		if (fail) {
			jobStatus.setStatus(Constants.JOB_FAILED);
			String remark = jobStatus.getRemark();
			remark = remark + "Job: " + jobName + " failed with exit code: " + statusCode;
			jobStatus.setRemark(remark);
		} else {
			jobStatus.setStatus(Constants.JOB_SUCCESS);
		}

		jobStatus.setEndTime(new Date());
		jobStatusRepo.save(jobStatus);
	}

}
