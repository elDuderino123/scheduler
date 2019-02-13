package com.system.job.scheduler.agent.services;

import com.system.job.scheduler.agent.model.ExecutionResponse;
import com.system.job.scheduler.agent.model.JobDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class JobExecutorService {

	@Autowired
	RestTemplate restTemplate;

	public ExecutionResponse executeCommand(JobDetails jobDetails) {

		ExecutionResponse response = new ExecutionResponse();
		
		response.withJobId(jobDetails.getJobId())
				.withJobLaunchId(jobDetails.getJobLaunchId());
				
		
		// Form the command by appending arguents at the end
		List<String> command = new ArrayList<>();

		command.add(jobDetails.getScriptExecutionCmd());

		jobDetails.getArguments().forEach(arg -> {
			command.add(arg);
		});

		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.redirectErrorStream(true);

		processBuilder.command(command);

		try {
			Process process = processBuilder.start();

			StringBuilder output = new StringBuilder();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

			int exitVal = process.waitFor();
			if (exitVal == 0) {
				response.withResponseCode(0)
						.withResponse("Success")
						.withSuccessMessage(output.toString());
			} else {
				response.withResponseCode(exitVal)
						.withResponse("Failure")
						.withErrorMessage(output.toString());
			}
		} catch (IOException | InterruptedException e) {
			response.withResponseCode(-1);
			e.printStackTrace();

			//TODO
			//Attempt retry up-to retry count
			//If retry fails attempt to clean up

		}
		
		return response;

	}


	public void executeCommandAsync(JobDetails jobDetails){

		CompletableFuture.supplyAsync(()->{
			return executeCommand(jobDetails);
		}).thenAcceptAsync(res->{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<ExecutionResponse> request = new HttpEntity<>(res, headers);

			
		});

	}

}
