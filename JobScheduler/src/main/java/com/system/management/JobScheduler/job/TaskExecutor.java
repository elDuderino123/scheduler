package com.system.management.JobScheduler.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.system.management.JobScheduler.commons.Constants;
import com.system.management.JobScheduler.db.entity.JobStatus;
import com.system.management.JobScheduler.db.repository.JobStatusRepo;
import com.system.management.JobScheduler.model.execution.ServerInfo;
import com.system.management.JobScheduler.model.execution.StageExecDetail;
import com.system.management.JobScheduler.model.registration.JobExecutionDetails;
import com.system.management.JobScheduler.model.registration.JobRegistrationDto;
import com.system.management.JobScheduler.services.CacheService;
import com.system.management.JobScheduler.services.JobDetailsService;
import com.system.management.JobScheduler.services.JschExecutorService;
import com.system.management.JobScheduler.services.LockingService;
import com.system.management.JobScheduler.services.MailService;

@Service
public class TaskExecutor extends QuartzJobBean {

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

	@Autowired
	private JobDetailsService jobDetailsService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

		JobDataMap jobDataMap = context.getMergedJobDataMap();
		String jobName = jobDataMap.getString("jobName");
		JobRegistrationDto dto = jobDetailsService.getJobDetails(jobName);

		final UUID jobLaunchId = Generators.timeBasedGenerator().generate();
		final Date startTime = new Date();

		JobStatus jobStatus = new JobStatus();
		jobStatus.setJobLaunchId(jobLaunchId);
		jobStatus.setStartTime(new Date());
		jobStatus.setStatus(Constants.JOB_STARTED);
		jobStatus.setType(Constants.STANDALONE_JOB);
		jobStatus.setName(dto.getJobName());

		// Make DB Entry
		jobStatusRepo.save(jobStatus);

		// Execute stage
		executeStage(dto, startTime, jobLaunchId);
	}

	public void executeStage(JobRegistrationDto dto, Date startTime, UUID jobLaunchId) {
		final JobExecutionDetails jobExecDetails = dto.getJobExceutionDetails();
		dto.getServers().forEach(serverDetails -> {

			String host = serverDetails.getHost();
			int port = serverDetails.getPort();
			String user = serverDetails.getUser();
			String password = serverDetails.getPassword();

			StringBuilder command = new StringBuilder();

			command.append(jobExecDetails.getBashCommand()).append(" ");
			command.append(dto.getJobName()).append(" "); // arg 1 - Job Name
			command.append(Constants.STANDALONE_JOB).append(" ");// arg 2 - Launch Id
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
			// Obtain the lock for launch id
			lockingService.getLockForLuanchId(jobLaunchId);

			String jobStr = cacheService.getForUUID(jobLaunchId);
			ObjectMapper mapper = new ObjectMapper();

			StageExecDetail stageDetails = null;
			if (jobStr != null) {
				stageDetails = mapper.readValue(jobStr, StageExecDetail.class);
				List<ServerInfo> servers = stageDetails.getHosts();
				ServerInfo sInfo = new ServerInfo();
				sInfo.setStartTime(new Date().getTime());
				sInfo.setServerName(host);
				servers.add(sInfo);
			} else {

				List<ServerInfo> servers = new ArrayList<>();
				ServerInfo sInfo = new ServerInfo();
				sInfo.setStartTime(new Date().getTime());
				sInfo.setServerName(host);
				servers.add(sInfo);

				stageDetails = new StageExecDetail();
				stageDetails.setName(dto.getJobName());
				stageDetails.setStartTime(startTime.getTime());
				stageDetails.setHosts(servers);
			}

			jobStr = mapper.writeValueAsString(stageDetails);
			cacheService.setForUUID(jobLaunchId, jobStr);
		} finally {
			// Release the lock
			lockingService.releaseLockForLaunchId(jobLaunchId);
		}

	}

}
