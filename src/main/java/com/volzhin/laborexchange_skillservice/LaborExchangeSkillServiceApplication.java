package com.volzhin.laborexchange_skillservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class LaborExchangeSkillServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaborExchangeSkillServiceApplication.class, args);
    }

}
