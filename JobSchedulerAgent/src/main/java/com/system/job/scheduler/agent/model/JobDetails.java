package com.system.job.scheduler.agent.model;

import java.util.List;
import java.util.UUID;

public class JobDetails {

    private int jobId;
    private UUID jobLaunchId;
    private String scriptExecutionCmd;
    private Integer argCount;
    private List<String> arguments;
    private String recoveryCommand;
    private String cleanupCommand;
    private Integer retryCount;


    public int getJobId() {
        return jobId;
    }

    public JobDetails withJobId(int jobId) {
        this.jobId = jobId;
        return this;
    }

    public UUID getJobLaunchId() {
        return jobLaunchId;
    }

    public JobDetails withJobLaunchId(UUID jobLaunchId) {
        this.jobLaunchId = jobLaunchId;
        return this;
    }

    public String getScriptExecutionCmd() {
        return scriptExecutionCmd;
    }

    public JobDetails withScriptExecutionCmd(String scriptExecutionCmd) {
        this.scriptExecutionCmd = scriptExecutionCmd;
        return this;
    }

    public Integer getArgCount() {
        return argCount;
    }

    public JobDetails withArgCount(Integer argCount) {
        this.argCount = argCount;
        return this;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public JobDetails withArgument(List<String> arguments) {
        this.arguments = arguments;
        return this;
    }

    public JobDetails withArguments(List<String> arguments) {
        this.arguments = arguments;
        return this;
    }

    public String getRecoveryCommand() {
        return recoveryCommand;
    }

    public JobDetails withRecoveryCommand(String recoveryCommand) {
        this.recoveryCommand = recoveryCommand;
        return this;
    }

    public String getCleanupCommand() {
        return cleanupCommand;
    }

    public JobDetails withCleanupCommand(String cleanupCommand) {
        this.cleanupCommand = cleanupCommand;
        return this;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public JobDetails withRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
        return this;
    }

}
