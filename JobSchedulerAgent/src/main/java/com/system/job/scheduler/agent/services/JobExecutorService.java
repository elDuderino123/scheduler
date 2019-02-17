package com.system.job.scheduler.agent.services;

import com.system.job.scheduler.agent.model.ExecutionResponse;
import com.system.job.scheduler.agent.model.JobDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class JobExecutorService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Environment env;

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

				//Execution failed

				//Attempt retry
				if(jobDetails.getRetryCount() > 0){
					jobDetails.withRetryCount(jobDetails.getRetryCount()-1);
					return executeCommand(jobDetails);
				}else{
					//Attempt recovery
					//Its imperative that input args of the recovery arg is same as actual command
					jobDetails.withScriptExecutionCmd(jobDetails.getRecoveryCommand());
					jobDetails.withRetryCount(0);
					return executeCommand(jobDetails);

				}
		}
		
		return response;

	}


	public void executeCommandAsync(JobDetails jobDetails){

		CompletableFuture.supplyAsync(()->{
			return executeCommand(jobDetails);
		}).thenAcceptAsync(res->{
			try {

				if(res.getResponseCode() > 0){
					//Execution failed

					//Attempt retry
					if(jobDetails.getRetryCount() > 0){
						jobDetails.withRetryCount(jobDetails.getRetryCount()-1);
						executeCommandAsync(jobDetails);
						return;
					}else{
						//Attempt recovery
						//Its imperative that input args of the recovery arg is same as actual command
						jobDetails.withScriptExecutionCmd(jobDetails.getRecoveryCommand());
						jobDetails.withRetryCount(0);
						executeCommandAsync(jobDetails);
						return ;

					}
				}
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				HttpEntity<ExecutionResponse> request = new HttpEntity<>(res, headers);

				ResponseEntity<String> response = restTemplate.postForEntity(new URI(env.getProperty("host.url")),request,String.class);

				if(response.getStatusCode().isError()){

					Thread.sleep(10*1000L);

					//Retry sending response
					response = restTemplate.postForEntity(new URI(env.getProperty("host.url")),request,String.class);

					System.out.println(response.getStatusCode()+" | "+response.getStatusCode().getReasonPhrase()+" | "+ response.getBody());

				}
			} catch (URISyntaxException | InterruptedException e) {
				e.printStackTrace();
			}


		});

	}

}
