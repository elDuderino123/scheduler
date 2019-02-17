package com.system.management.JobScheduler.model.registration;

import java.io.Serializable;
import java.util.List;

public class JobExecutionDetails implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String bashCommand;
	private Integer argCount;
	private List<String> arguments;
	private String logRedirectFile ;
	
	public String getBashCommand() {
		return bashCommand;
	}
	public void setBashCommand(String bashCommand) {
		this.bashCommand = bashCommand;
	}
	public Integer getArgCount() {
		return argCount;
	}
	public void setArgCount(Integer argCount) {
		this.argCount = argCount;
	}
	public List<String> getArguments() {
		return arguments;
	}
	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}
	public String getLogRedirectFile() {
		return logRedirectFile;
	}
	public void setLogRedirectFile(String logRedirectFile) {
		this.logRedirectFile = logRedirectFile;
	}
	
	
	

}
