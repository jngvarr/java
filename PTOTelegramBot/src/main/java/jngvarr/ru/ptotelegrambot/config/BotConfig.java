package jngvarr.ru.ptotelegrambot.config;

import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
//@ConfigurationProperties(prefix = "telegram.bot")
@PropertySource("application.yml")
public class BotConfig {
    private String botName;
    private String botToken;

}
