package ru.jngvarr.authservice.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "dao")
@EnableJpaRepositories(basePackages = "ru.jngvarr.authservice.repositories")
public class JpaConfig {

}
