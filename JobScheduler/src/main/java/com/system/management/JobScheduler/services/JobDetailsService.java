package com.system.management.JobScheduler.services;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.management.JobScheduler.commons.Constants;
import com.system.management.JobScheduler.db.entity.JobDetails;
import com.system.management.JobScheduler.db.repository.JobDetailsRepo;
import com.system.management.JobScheduler.job.PipelineExecutor;
import com.system.management.JobScheduler.job.TaskExecutor;
import com.system.management.JobScheduler.model.registration.JobExecutionDetails;
import com.system.management.JobScheduler.model.registration.JobRegistrationDto;
import com.system.management.JobScheduler.model.registration.PipelineRegistrationDto;

@Service
public class JobDetailsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(JobDetailsService.class);

	private Pattern pattern;

	@PostConstruct
	public void init() {
		pattern = Pattern.compile(Constants.EMAIL_PATTERN);
	}

	@Autowired
	private JobDetailsRepo jobDetailsRepo;

	public JobRegistrationDto getJobDetails(String jobName) {
		JobRegistrationDto jobRegDetails = null;
		try {
			JobDetails jobData = jobDetailsRepo.findById(jobName).get();

			String jobDetailsStr = jobData.getJobRegDetails();
			ObjectMapper mapper = new ObjectMapper();

			jobRegDetails = mapper.readValue(jobDetailsStr, JobRegistrationDto.class);
		} catch (Exception e) {
			LOGGER.error("Error while fetching the job details", e);
		}

		return jobRegDetails;

	}

	public void persistJobRegDetails(JobRegistrationDto jobDto , String jobKey , String triggerKey) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = mapper.writeValueAsString(jobDto);

			JobDetails jobDetails = new JobDetails();
			jobDetails.setJobName(jobDto.getJobName());
			jobDetails.setJobRegDetails(jsonString);
			jobDetails.setQuartzJobKey(jobKey);
			jobDetails.setQuartzTriggerKey(triggerKey);
			jobDetails.setIsActive("true");

			jobDetailsRepo.save(jobDetails);

		} catch (Exception e) {
			LOGGER.error("Exception while persisting the job registration details");
			throw new IllegalStateException("Not able to persist job : Contact support", e);
		}
	}

	public void validateJobRegistrationDetails(JobRegistrationDto jobDto) {

		// Validate If Job Name already exists
		if (StringUtils.isBlank(jobDto.getJobName())) {
			throw new IllegalArgumentException("Unique job name is mandetory");
		}

		Optional<JobDetails> opt = jobDetailsRepo.findById(jobDto.getJobName());
		if (opt.isPresent()) {
			JobDetails jobDetails = opt.get();
			String alreadyRegistered = jobDetails.getJobRegDetails();
			throw new IllegalArgumentException("Already Registered: " + alreadyRegistered);
		}

	}

	public void validateJobDetailsJson(JobRegistrationDto jobDto) {

		StringBuilder validationErrors = new StringBuilder();

		// Validate To Email
		if (!pattern.matcher(jobDto.getEmailTo()).matches()) {
			validationErrors.append("Invalid To Email").append(System.lineSeparator());
		}

		// Validate CC Email
		if (jobDto.getEmailCc() != null && !pattern.matcher(jobDto.getEmailCc()).matches()) {
			validationErrors.append("Invalid Cc Email").append(System.lineSeparator());
		}

		// Validate Job Type
		if (!StringUtils.isBlank(jobDto.getIsTask())) {
			if (Boolean.parseBoolean(jobDto.getIsTask())) {
				// Validate cron expression
				if (!CronExpression.isValidExpression(jobDto.getCronExpression())) {
					validationErrors.append("Invalid Cron Expression").append(System.lineSeparator());
				}

				// Check if its not stage
				if (!StringUtils.isBlank(jobDto.getIsStage())) {
					if (Boolean.parseBoolean(jobDto.getIsStage())) {
						validationErrors.append("A job can be either standalone or a pipeline step");
					}
				}
			} else {
				// Check if its not stage
				if (!StringUtils.isBlank(jobDto.getIsStage())) {
					if (!Boolean.parseBoolean(jobDto.getIsStage())) {
						validationErrors.append("A job can be either standalone or a pipeline step");
					}
				} else {
					validationErrors.append("A job can be either standalone or a pipeline step");
				}
			}
		} else {
			// Check if its not stage
			if (!StringUtils.isBlank(jobDto.getIsStage())) {
				if (!Boolean.parseBoolean(jobDto.getIsStage())) {
					validationErrors.append("A job can be either standalone or a pipeline step");
				}
			} else {
				validationErrors.append("A job can be either standalone or a pipeline step");
			}
		}

		// Validate arguments
		JobExecutionDetails jobExec = jobDto.getJobExceutionDetails();

		if (jobExec.getArgCount() != jobExec.getArguments().size()) {
			validationErrors.append("Invalid argument count").append(System.lineSeparator());
		}

		if (validationErrors.length() > 0) {
			throw new IllegalArgumentException("ERROR: \n" + validationErrors.toString());
		}

		if (validationErrors.length() > 0) {
			throw new IllegalArgumentException(validationErrors.toString());
		}

	}

	public void updateJobDetails(JobRegistrationDto dto) {
		try {
			Optional<JobDetails> opt = jobDetailsRepo.findById(dto.getJobName());
			if (!opt.isPresent()) {
				throw new IllegalArgumentException("Job Doesn't exist: " + dto.getJobName());
			}

			// Validate Job
			validateJobDetailsJson(dto);

			JobDetails job = opt.get();

			ObjectMapper mapper = new ObjectMapper();

			job.setJobRegDetails(mapper.writeValueAsString(dto));
			
		} catch (Exception e) {
			LOGGER.error("Job update failed");
			throw new RuntimeException(e);
		}

	}
	
	public void removeJobDetails(String jobName) {
		
		//TODO Wait for the job to finish
		
		//Remove from repo
		jobDetailsRepo.deleteById(jobName);
		
	}
	
	public  JobDetail createJob(String jobName , boolean isPipeline) {
        JobDataMap jobDataMap = new JobDataMap();

        if(isPipeline) {
        	
        	jobDataMap.put("pipelineName", jobName);
        	return JobBuilder.newJob(PipelineExecutor.class)
                    .withIdentity(UUID.randomUUID().toString(), "scheduled-jobs")
                    .withDescription(jobName)
                    .usingJobData(jobDataMap)
                    .storeDurably()
                    .build();
        	
        }
        
        jobDataMap.put("jobName", jobName);

        return JobBuilder.newJob(TaskExecutor.class)
                .withIdentity(UUID.randomUUID().toString(), "scheduled-jobs")
                .withDescription(jobName)
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }
	
	public Trigger buildJobTrigger(JobDetail jobDetail, String cronExpression) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "scheduled-job-triggers")
                .withDescription("scheduled-job-triggers")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }
	
}
