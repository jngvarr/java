package jngvarr.ru.ptotelegrambot;

import jngvarr.ru.ptotelegrambot.config.BotConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
//@EnableConfigurationProperties(BotConfig.class)
public class PtoTelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(PtoTelegramBotApplication.class, args);
    }
}
