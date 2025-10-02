package com.example.society;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.example.society") // Must include guest package
public class SocietyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SocietyApplication.class, args);
    }
}
