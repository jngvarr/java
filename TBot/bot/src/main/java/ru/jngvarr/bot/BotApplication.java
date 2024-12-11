package ru.jngvarr.bot;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class BotApplication {
    @PostConstruct
    public void logEnvironmentVariables() {
        System.out.println("TELEGRAM_BOT_TOKEN: " + System.getenv("TELEGRAM_BOT_TOKEN"));
        System.out.println("TELEGRAM_BOT_OWNER: " + System.getenv("TELEGRAM_BOT_OWNER"));
    }

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
