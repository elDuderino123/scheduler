package com.system.management.JobScheduler.commons;

public class Constants {
	
	
	public static final String EMAIL_PATTERN = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
	public static final String FROM_EMAIL = "admin@admin.com";
	public static final String JOB_STARTED = "started";
	public static final String JOB_SUCCESS = "success";
	public static final String JOB_FAILED = "failed";
	public static final String STANDALONE_JOB = "Task";
	public static final String PIPELINE_JOB = "Pipeline";
	public static final String CACHE_PERSISTENCE_FILE = "jobSchedulerLocalCahche.dat";
}
