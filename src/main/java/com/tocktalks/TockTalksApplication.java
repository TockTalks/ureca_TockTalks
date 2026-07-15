package com.tocktalks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TockTalksApplication {

    public static void main(String[] args) {
        SpringApplication.run(TockTalksApplication.class, args);
    }
}
