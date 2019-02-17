package com.system.management.JobScheduler.commons;

public class CommonUtils {

	public static String generateMailContentForFailure(String job , String server , int statusCode ) {
		StringBuilder errorMsg = new StringBuilder();
		
		errorMsg.append("Job Name").append(" : ").append(job).append(System.lineSeparator());
		errorMsg.append("Server").append(" : ").append(server).append(System.lineSeparator());
		errorMsg.append("Status Code").append(" : ").append(statusCode).append(System.lineSeparator());
		return errorMsg.toString();
	}
}
