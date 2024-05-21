package ru.jngvarr.authservice.services;

import dao.entities.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.jngvarr.authservice.repositories.RefreshTokenRepository;
import ru.jngvarr.authservice.repositories.UserRepository;
import security_config.JwtUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository tokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public void saveRefreshToken(String refreshToken) {
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken(refreshToken);
        newRefreshToken.setExpiryDate(convertToLocalDateTime(jwtUtil.extractExpiration (refreshToken)));
        refreshTokenRepository.save(newRefreshToken);
    }

    public boolean validateRefreshToken(String refreshToken) {
        UserDetails userDetails = new UserDetailsServiceImpl(userRepository)
                .loadUserByUsername(jwtUtil.extractUsername(refreshToken));
        return jwtUtil.validateToken(refreshToken, userDetails);
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
    public RefreshToken findByToken(String token){
        return tokenRepository.findByToken(token);
    }
}
