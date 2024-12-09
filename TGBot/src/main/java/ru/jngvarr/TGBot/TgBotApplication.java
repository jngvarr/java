package ru.jngvarr.TGBot;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.jngvarr.TGBot.config.BotConfig;

@SpringBootApplication

public class TgBotApplication {
	@PostConstruct
	public void logEnvironmentVariables() {
		System.out.println("TELEGRAM_BOT_TOKEN: " + System.getenv("TELEGRAM_BOT_TOKEN"));
		System.out.println("TELEGRAM_BOT_OWNER: " + System.getenv("TELEGRAM_BOT_OWNER"));
	}
	public static void main(String[] args) {
		SpringApplication.run(TgBotApplication.class, args);

	}
}
