package jngvarr.ru.pto_ackye_rzhd.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class BotConfig {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.owner}")
    private long ownerId;

    @Value("${telegram.bot.name}")
    private String botName;

}
