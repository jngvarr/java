package ru.jngvarr.clientmanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.jngvarr.clientmanagement.auth.JwtAuthorizationFilter;

import static org.springframework.security.config.Elements.JWT;

@Configuration
@EnableWebSecurity
public class securityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;
    @Override // настройка правил безопасности
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Отключаем защиту CSRF, так как будем использовать JWT
                .authorizeRequests()
                .antMatchers("/login").permitAll() // Позволяем всем пользователям доступ к эндпоинту "/login"
                .anyRequest().authenticated() // Все остальные эндпоинты требуют аутентификации
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager())) //Добавляем наш фильтр аутентификации
                .addFilterBefore(new JwtAuthorizationFilter(authenticationManager(), //Добавляем наш фильтр авторизации
                        UsernamePasswordAuthenticationFilter.class))
                        .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Не создаем сессию, так как будем использовать JWT
    }
}
