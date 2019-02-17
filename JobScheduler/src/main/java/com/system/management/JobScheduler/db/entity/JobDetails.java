package com.system.management.JobScheduler.db.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="jobdetails")
public class JobDetails implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="jobname")
	private String jobName ;
	
	@Column(name="jobregdetails")
	private String jobRegDetails;
	
	@Column(name="active")
	private String isActive;
	
	@Column(name="quartzjobkey")
	private String quartzJobKey;
	
	@Column(name="quartztriggerkey")
	private String quartzTriggerKey;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobRegDetails() {
		return jobRegDetails;
	}

	public void setJobRegDetails(String jobRegDetails) {
		this.jobRegDetails = jobRegDetails;
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
