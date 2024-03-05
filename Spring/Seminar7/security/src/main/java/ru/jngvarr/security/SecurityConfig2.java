package ru.jngvarr.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface SecurityConfig2 {
    void conﬁgure(HttpSecurity http) throws Exception;
}
