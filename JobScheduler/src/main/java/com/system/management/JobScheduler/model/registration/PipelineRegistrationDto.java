package com.system.management.JobScheduler.model.registration;

import java.io.Serializable;
import java.util.List;

public class PipelineRegistrationDto implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String pipelineName;
	private String toEmail;
	private String cronExpression;
	private List<StageSequenceDetail> sequence;
	
	
	public String getPipelineName() {
		return pipelineName;
	}
	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}
	public List<StageSequenceDetail> getSequence() {
		return sequence;
	}
	public void setSequence(List<StageSequenceDetail> sequence) {
		this.sequence = sequence;
	}
	public String getToEmail() {
		return toEmail;
	}
	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	
	
	
	
	
}
