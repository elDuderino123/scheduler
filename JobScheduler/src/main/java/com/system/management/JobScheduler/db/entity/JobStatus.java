package com.system.management.JobScheduler.db.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="jobstatus")
public class JobStatus {
	
	@Id
	@Column(name="joblaunchid",nullable=false)
	private UUID jobLaunchId;
	
	@Column(name="jobtype",nullable=false)
	private String type;
	
	@Column(name="jobname",nullable=false)
	private String name;
	
	@Column(name="startts",columnDefinition="timstamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startTime;
	
	@Column(name="endts",columnDefinition="timstamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;
	
	@Column(name="status",nullable=false)
	private String status;
	
	@Column(name="remark",nullable=false)
	private String remark;

	public UUID getJobLaunchId() {
		return jobLaunchId;
	}

	public void setJobLaunchId(UUID jobLaunchId) {
		this.jobLaunchId = jobLaunchId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
}
