package com.gachonoj.problemservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ProblemServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProblemServiceApplication.class, args);
    }

}
