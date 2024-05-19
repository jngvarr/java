package ru.jngvarr.clientmanagement.auth.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AuthenticationResponse {
    private final String accessToken;
//    private final String refreshToken;
}
