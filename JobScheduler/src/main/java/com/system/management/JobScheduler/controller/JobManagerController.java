package com.system.management.JobScheduler.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.system.management.JobScheduler.model.registration.JobRegistrationDto;
import com.system.management.JobScheduler.model.registration.PipelineRegistrationDto;
import com.system.management.JobScheduler.services.JobDetailsService;
import com.system.management.JobScheduler.services.JobManager;
import com.system.management.JobScheduler.services.PipelineDetailsService;

@RestController
public class JobManagerController {
	
	@Autowired
	private JobManager jobManager;
	
	@Autowired
	private JobDetailsService jobDetailsService;
	
	@Autowired
	private PipelineDetailsService pipelineDetailsService;
	
	
	@PostMapping("/api/v1/register/pipeline")
	public ResponseEntity<String> registerPipeline(@RequestBody PipelineRegistrationDto pipelineRegDetails) {
		try {
			jobManager.registerPipeline(pipelineRegDetails);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ExceptionUtils.getStackTrace(e));
		}
	}
	
	@PostMapping("/api/v1/register/job")
	public ResponseEntity<String> registerJob(@RequestBody JobRegistrationDto jobRegDetails) {
		try {
			jobManager.registerJob(jobRegDetails);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ExceptionUtils.getStackTrace(e));
		}
	}
	
	@GetMapping("/api/v1/job/{jobName}")
	public ResponseEntity<JobRegistrationDto> getJobDetails(@PathVariable String jobName){
		JobRegistrationDto dto = jobDetailsService.getJobDetails(jobName);
		
		return ResponseEntity.ok().body(dto);
	}
	
	@GetMapping("/api/v1/job/{pipelineName}")
	public ResponseEntity<PipelineRegistrationDto> getPipelineDetails(@PathVariable String pipelineName){
		
		PipelineRegistrationDto dto = pipelineDetailsService.getPipelineDetails(pipelineName);
		return ResponseEntity.ok().body(dto);
	}
	
	@PutMapping("/api/v1/update/pipeline")
	public void updatePipeline(@RequestBody PipelineRegistrationDto pipelineRegDetails) {
		
	}
	
	@PutMapping("/api/v1/update/job")
	public void updateJob(@RequestBody JobRegistrationDto jobRegDetails) {
		
	}
	
	@PutMapping("/api/v1/reschedule/pipeline")
	public void reschedulePipeline(@RequestBody PipelineRegistrationDto pipelineRegDetails) {
		
	}
	
	@PutMapping("/api/v1/reschedule/job")
	public void rescheduleJob(@RequestBody JobRegistrationDto jobRegDetails) {
		
	}
	
	@PutMapping("/api/v1/disable/pipeline/{pipelineName}")
	public void disablePipeline(@PathVariable String pipelineName) {
		
	}
	
	@PutMapping("/api/v1/disable/job/{jobName}")
	public void disablejob(@PathVariable String jobName) {
		
	}
	
}
