package com.orfangenes.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@SpringBootApplication(scanBasePackages = "com.orfangenes.repo.entity")
public class OrfanidApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrfanidApplication.class, args);
    }
}
