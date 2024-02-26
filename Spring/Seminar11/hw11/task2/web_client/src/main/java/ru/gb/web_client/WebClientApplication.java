package ru.gb.web_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class WebClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebClientApplication.class, args);
    }
}