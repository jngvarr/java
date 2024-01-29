package com.example.demo.config;

import com.example.demo.model.MagicData;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "magic.data")
public class AppConfig {
    private MagicData magicData;
}
