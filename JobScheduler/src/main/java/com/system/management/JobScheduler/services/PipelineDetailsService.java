package com.system.management.JobScheduler.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.management.JobScheduler.db.entity.JobDetails;
import com.system.management.JobScheduler.db.entity.PipelineDetails;
import com.system.management.JobScheduler.db.repository.JobDetailsRepo;
import com.system.management.JobScheduler.db.repository.PipelineDetailsRepo;
import com.system.management.JobScheduler.model.registration.PipelineRegistrationDto;

@Service
public class PipelineDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PipelineDetailsService.class);
	
	@Autowired
	private PipelineDetailsRepo pipelineDetailsRepo ;
	
	@Autowired
	private JobDetailsRepo jobDetailsRepo;
	
	public PipelineRegistrationDto getPipelineDetails(String piplineName) {
		PipelineRegistrationDto pipeLineRegDto = null;
		try {
			PipelineDetails details = pipelineDetailsRepo.findById(piplineName).get();
			ObjectMapper mapper = new ObjectMapper();
			pipeLineRegDto = mapper.readValue(details.getPipelineRegDetails(), PipelineRegistrationDto.class);
		} catch(Exception e) {
			LOGGER.error("Exception while fetching the pipeline details",e);
		}
		
		return pipeLineRegDto;
	}
	
	public void persistPipelineDetails(PipelineRegistrationDto dto , String jobKey , String triggerKey) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonStr = mapper.writeValueAsString(dto);
			
			PipelineDetails details = new PipelineDetails();
			details.setPipelineName(dto.getPipelineName());
			details.setPipelineRegDetails(jsonStr);
			details.setQuartzJobKey(jobKey);
			details.setQuartzTriggerKey(triggerKey);
			details.setIsActive("true");
			
			pipelineDetailsRepo.save(details);
		}catch(Exception e) {
			LOGGER.error("Exception while persisting the pipeline details ",e);
			throw new IllegalStateException("Not able to persist job : Contact support" , e);
		}
	}
	
	public void validatePipelineDetails(PipelineRegistrationDto dto) {
		
		StringBuilder validationResponse = new StringBuilder();
		
		dto.getSequence().forEach(stage ->{
			Optional<JobDetails> opt = jobDetailsRepo.findById(stage.getStageName());
			if(!opt.isPresent()) {
				validationResponse.append("Pipeline stage: "+stage.getStageName()+" doen't exist").append(System.lineSeparator());
			}
		});
		
		if(validationResponse.length() > 0) {
			throw new IllegalArgumentException("Error: "+validationResponse.toString());
		}
	}
	
	public PipelineDetails getInternalPipelineDetails(String pipelineName) {
		try {
			PipelineDetails details = pipelineDetailsRepo.findById(pipelineName).get();
		} catch(Exception e ) {
			LOGGER.error("Exception while fteching the pipeline details: "+ pipelineName,e);
		}
		return null;
		
	}
	

}
