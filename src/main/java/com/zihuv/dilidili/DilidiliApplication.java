package com.zihuv.dilidili;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DilidiliApplication {

    public static void main(String[] args) {
        SpringApplication.run(DilidiliApplication.class, args);
    }

}
