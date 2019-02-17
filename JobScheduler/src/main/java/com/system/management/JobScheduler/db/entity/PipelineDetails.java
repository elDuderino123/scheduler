package com.system.management.JobScheduler.db.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="pipelinedetails")
public class PipelineDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="pipelinename")
	private String pipelineName;
	
	@Column(name="pipelineregdetails")
	private String pipelineRegDetails;
	
	@Column(name = "active")
	private String isActive;

	@Column(name="quartzjobkey")
	private String quartzJobKey;
	
	@Column(name="quartztriggerkey")
	private String quartzTriggerKey;
	
	public String getPipelineName() {
		return pipelineName;
	}

	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}

	public String getPipelineRegDetails() {
		return pipelineRegDetails;
	}

	public void setPipelineRegDetails(String pipelineRegDetails) {
		this.pipelineRegDetails = pipelineRegDetails;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getQuartzJobKey() {
		return quartzJobKey;
	}

	public void setQuartzJobKey(String quartzJobKey) {
		this.quartzJobKey = quartzJobKey;
	}

	public String getQuartzTriggerKey() {
		return quartzTriggerKey;
	}

	public void setQuartzTriggerKey(String quartzTriggerKey) {
		this.quartzTriggerKey = quartzTriggerKey;
	}
	
	
}
