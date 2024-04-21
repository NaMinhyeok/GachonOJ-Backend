package com.gachonoj.problemservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableJpaAuditing
@EnableFeignClients
@EnableScheduling
@EnableAsync
@SpringBootApplication
public class ProblemServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProblemServiceApplication.class, args);
    }

}
