package com.system.management.JobScheduler.model.registration;

import java.io.Serializable;
import java.util.List;

public class JobRegistrationDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String jobName ;
	private String emailTo;
	private String emailCc;
	private String isTask;
	private String isStage;
	private String cronExpression;
	private String onError;
	private String errorAlert;
	private String successAlert;
	private String errorMessage;
	private List<ServerDetails> servers;
	private JobExecutionDetails jobExceutionDetails;
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getEmailTo() {
		return emailTo;
	}
	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}
	public String getEmailCc() {
		return emailCc;
	}
	public void setEmailCc(String emailCc) {
		this.emailCc = emailCc;
	}
	public String getIsTask() {
		return isTask;
	}
	public void setIsTask(String isTask) {
		this.isTask = isTask;
	}
	public String getIsStage() {
		return isStage;
	}
	public void setIsStage(String isStage) {
		this.isStage = isStage;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	public String getOnError() {
		return onError;
	}
	public void setOnError(String onError) {
		this.onError = onError;
	}
	public String getErrorAlert() {
		return errorAlert;
	}
	public void setErrorAlert(String errorAlert) {
		this.errorAlert = errorAlert;
	}
	public String getSuccessAlert() {
		return successAlert;
	}
	public void setSuccessAlert(String successAlert) {
		this.successAlert = successAlert;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public List<ServerDetails> getServers() {
		return servers;
	}
	public void setServers(List<ServerDetails> servers) {
		this.servers = servers;
	}
	public JobExecutionDetails getJobExceutionDetails() {
		return jobExceutionDetails;
	}
	public void setJobExceutionDetails(JobExecutionDetails jobExceutionDetails) {
		this.jobExceutionDetails = jobExceutionDetails;
	}
	
}
