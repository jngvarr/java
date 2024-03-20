package ru.jngvarr.storagemanagement.config;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "dao")
@EnableJpaRepositories(basePackages = "ru.jngvarr.storagemanagement.repositories")
public class JpaConfig {

}