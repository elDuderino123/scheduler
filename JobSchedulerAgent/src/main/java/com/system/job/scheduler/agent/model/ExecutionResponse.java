package com.system.job.scheduler.agent.model;

import java.io.Serializable;
import java.util.UUID;

public class ExecutionResponse implements Serializable {


    private Integer jobId ;
    private UUID jobLaunchId;
    private Integer responseCode;
    private String response;
    private String successMessage ;
    private String errorMessage;

    public Integer getResponseCode() {
        return responseCode;
    }

    public ExecutionResponse withResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public String getResponse() {
        return response;
    }

    public ExecutionResponse withResponse(String response) {
        this.response = response;
        return this;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public ExecutionResponse withSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ExecutionResponse withErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public Integer getJobId() {
        return jobId;
    }

    public ExecutionResponse withJobId(Integer jobId) {
        this.jobId = jobId;
        return this;
    }

    public UUID getJobLaunchId() {
        return jobLaunchId;
    }

    public ExecutionResponse withJobLaunchId(UUID jobLaunchId) {
        this.jobLaunchId = jobLaunchId;
        return this;
    }

    @Override
    public String toString() {
        return "ExecutionResponse{" +
                "jobId=" + jobId +
                ", jobLaunchId=" + jobLaunchId +
                ", responseCode=" + responseCode +
                ", response='" + response + '\'' +
                ", successMessage='" + successMessage + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
