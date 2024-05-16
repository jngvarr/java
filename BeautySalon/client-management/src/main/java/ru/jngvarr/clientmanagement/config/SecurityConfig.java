package ru.jngvarr.clientmanagement.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.jngvarr.clientmanagement.services.UserDetailsServiceImpl;

@Data
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                                .requestMatchers("/users/create").permitAll()
                                .requestMatchers("/login").permitAll()
//                .requestMatchers("/public/**").permitAll()
//                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
//                ).formLogin(form -> form
//                        .loginPage("/login")
//                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
