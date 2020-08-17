package com.orfangenes.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.orfangenes.common.models")
public class OrfanidApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrfanidApplication.class, args);
    }
}
