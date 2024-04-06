package com.gachonoj.memeberservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MemeberServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemeberServiceApplication.class, args);
    }

}
