package com.system.job.scheduler.agent.controller;

import com.system.job.scheduler.agent.model.ExecutionResponse;
import com.system.job.scheduler.agent.model.JobDetails;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobExecutionControllerTest {

    @Autowired
    TestRestTemplate restTemplate;
    @Test
    public void testJObExecutor(){

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JobDetails jobDetails = new JobDetails().withJobId(1).withJobLaunchId(UUID.randomUUID())
                    .withArgCount(1)
                    .withArgument(Collections.singletonList("0"))
                    .withScriptExecutionCmd("/home/love/Misc/test.sh");

            HttpEntity<JobDetails> request = new HttpEntity<>(jobDetails, headers);

            URI uri = new URI("/api/v1/execute/job");

            ResponseEntity<ExecutionResponse> result = restTemplate.postForEntity(uri, request, ExecutionResponse.class);

            System.out.println(result.getStatusCode());
            System.out.println(result.getBody());

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
