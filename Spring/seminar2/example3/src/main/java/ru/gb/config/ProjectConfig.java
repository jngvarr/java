package ru.gb.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.gb.domain.Car;

@Configuration
@ComponentScan(basePackages = "ru.gb.domain")
public class ProjectConfig {

}
