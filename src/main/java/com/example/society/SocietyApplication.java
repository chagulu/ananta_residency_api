package com.example.society;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.society") // Must include guest package
@EnableScheduling // <-- Enable scheduled tasks
public class SocietyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SocietyApplication.class, args);
    }
}
