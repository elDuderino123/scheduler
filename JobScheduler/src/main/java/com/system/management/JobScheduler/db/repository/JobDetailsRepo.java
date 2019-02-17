package com.system.management.JobScheduler.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.management.JobScheduler.db.entity.JobDetails;

public interface JobDetailsRepo extends JpaRepository<JobDetails, String> {

}
