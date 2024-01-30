package com.example.demo;

import com.example.demo.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
/**
 * Основной класс приложения с точкой входа.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
//@EnableConfigurationProperties(AppConfig.class)
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
