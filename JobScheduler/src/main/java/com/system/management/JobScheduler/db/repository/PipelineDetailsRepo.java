package com.system.management.JobScheduler.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.management.JobScheduler.db.entity.PipelineDetails;

public interface PipelineDetailsRepo extends JpaRepository<PipelineDetails, String> {

}
