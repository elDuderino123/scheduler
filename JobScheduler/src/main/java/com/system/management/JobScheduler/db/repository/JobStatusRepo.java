package com.system.management.JobScheduler.db.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.management.JobScheduler.db.entity.JobStatus;

public interface JobStatusRepo extends JpaRepository<JobStatus, UUID>{

}
