package ru.jngvarr.configserver;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

//    @Value("${GITHUB_TOKEN}")
//    private String githubToken;

//    @PostConstruct
//    public void init() {
//        System.out.println("GitHub Token: " + githubToken);
//    }


    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}
