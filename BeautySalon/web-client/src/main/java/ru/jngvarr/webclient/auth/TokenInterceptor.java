package ru.jngvarr.webclient.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class TokenInterceptor implements RequestInterceptor {
    private final String token;

    public TokenInterceptor(String token) {
        this.token = token;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", "Bearer " + token);
    }
}
