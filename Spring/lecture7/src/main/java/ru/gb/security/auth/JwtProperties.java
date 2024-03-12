package ru.gb.security.auth;

public class JwtProperties {
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String SECRET = "yourSecretKey";
    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}

