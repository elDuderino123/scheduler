package com.system.job.scheduler.agent.controller;

import com.system.job.scheduler.agent.model.ExecutionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.job.scheduler.agent.model.JobDetails;
import com.system.job.scheduler.agent.services.JobExecutorService;

@RestController
public class JobExecutionController {
	
	@Autowired
	JobExecutorService jobExecutorService ;
	
	
	@RequestMapping(value="/api/v1/execute/job",method=RequestMethod.POST)
	@ResponseBody
	public ExecutionResponse execute(@RequestBody JobDetails jobDetails) {
		
		try {

			return jobExecutorService.executeCommand(jobDetails);



		}catch(Exception e){
			throw e;
		}
	}

	@RequestMapping(value="/api/v1/submit/job",method=RequestMethod.POST)
	@ResponseBody
	public void executeAsync(@RequestBody JobDetails jobDetails) {

		try {

			jobExecutorService.executeCommandAsync(jobDetails);



		}catch(Exception e){
			throw e;
		}
	}
}
