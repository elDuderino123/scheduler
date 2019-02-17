package com.system.management.JobScheduler.model.execution;

import java.io.Serializable;
import java.util.List;

public class StageExecDetail implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private Integer executionOrder;
	private Long startTime ;
	private Long endTime;
	private Long timeOut ;
	private List<ServerInfo> hosts;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getExecutionOrder() {
		return executionOrder;
	}
	public void setExecutionOrder(Integer executionOrder) {
		this.executionOrder = executionOrder;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public Long getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(Long timeOut) {
		this.timeOut = timeOut;
	}
	public List<ServerInfo> getHosts() {
		return hosts;
	}
	public void setHosts(List<ServerInfo> hosts) {
		this.hosts = hosts;
	}
	
	

}
