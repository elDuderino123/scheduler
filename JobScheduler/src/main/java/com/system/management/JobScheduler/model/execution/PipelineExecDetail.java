package com.system.management.JobScheduler.model.execution;

import java.io.Serializable;
import java.util.LinkedList;

public class PipelineExecDetail implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name ;
	
	private LinkedList<StageExecDetail> jobSequence;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedList<StageExecDetail> getJobSequence() {
		return jobSequence;
	}

	public void setJobSequence(LinkedList<StageExecDetail> jobSequence) {
		this.jobSequence = jobSequence;
	}
	
	

}
