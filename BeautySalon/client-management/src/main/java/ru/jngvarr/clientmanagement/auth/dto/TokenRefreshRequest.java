package ru.jngvarr.clientmanagement.auth.dto;

import lombok.Data;

@Data
    public class TokenRefreshRequest {
        private String username;
        private String refreshToken;
    }