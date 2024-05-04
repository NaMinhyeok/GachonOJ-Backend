package com.gachonoj.submissionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaAuditing
@SpringBootApplication
public class SubmissionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubmissionServiceApplication.class, args);
    }

}
