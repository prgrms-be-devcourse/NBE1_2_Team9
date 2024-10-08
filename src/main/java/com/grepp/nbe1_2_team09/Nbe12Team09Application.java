package com.grepp.nbe1_2_team09;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class Nbe12Team09Application {

    public static void main(String[] args) {
        SpringApplication.run(Nbe12Team09Application.class, args);
    }

}
