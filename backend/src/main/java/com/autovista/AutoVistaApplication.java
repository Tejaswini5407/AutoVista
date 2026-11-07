package com.autovista;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AutoVistaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoVistaApplication.class, args);
    }
}
