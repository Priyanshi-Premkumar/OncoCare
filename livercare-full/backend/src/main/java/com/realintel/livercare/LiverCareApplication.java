package com.realintel.livercare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LiverCareApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiverCareApplication.class, args);
    }
}
