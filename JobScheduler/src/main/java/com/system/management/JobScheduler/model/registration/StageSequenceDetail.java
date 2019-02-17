package com.system.management.JobScheduler.model.registration;

import java.io.Serializable;

public class StageSequenceDetail implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String stageName ;
	private Integer executionSequence;
	
	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	public Integer getExecutionSequence() {
		return executionSequence;
	}
	public void setExecutionSequence(Integer executionSequence) {
		this.executionSequence = executionSequence;
	}
	
	
	
	
}
