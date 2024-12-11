package ru.jngvarr.bot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
public class BotConfig {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.owner}")
    private long ownerId;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${telegram.bot.name}")
    private String botName;


    public void printConfig() {
        System.out.println("Bot Token: " + botToken);
        System.out.println("Bot Owner ID: " + ownerId);
        System.out.println("Database Username: " + dbUsername);
        System.out.println("Database Password: " + dbPassword);
    }
}
