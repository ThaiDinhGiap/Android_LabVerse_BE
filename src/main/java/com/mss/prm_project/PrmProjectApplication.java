package com.mss.prm_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PrmProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrmProjectApplication.class, args);
    }

}
