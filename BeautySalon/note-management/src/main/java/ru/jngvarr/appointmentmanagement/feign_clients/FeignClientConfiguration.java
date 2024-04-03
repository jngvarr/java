package ru.jngvarr.appointmentmanagement.feign_clients;

import feign.auth.BasicAuthRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
public class FeignClientConfiguration {
    @Value("${feign.client.config.clients.basic-auth.username}")
    private String username;
    @Value("${feign.client.config.clients.basic-auth.password}")
    private String password;

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor(username, password);
    }
}
