package ru.jngvarr.clientmanagement.services;

import dao.entities.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.jngvarr.clientmanagement.auth.JwtUtil;
import ru.jngvarr.clientmanagement.repositories.RefreshTokenRepository;
import ru.jngvarr.clientmanagement.repositories.UserRepository;

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
        newRefreshToken.setExpiryDate(jwtUtil.extractExpiration(refreshToken));
        refreshTokenRepository.save(newRefreshToken);
    }

    public boolean validateRefreshToken(String refreshToken) {
        UserDetails userDetails = new UserDetailsServiceImpl(userRepository)
                .loadUserByUsername(jwtUtil.extractUsername(refreshToken));
        return jwtUtil.validateToken(refreshToken, userDetails);
    }

    public RefreshToken findByToken(String token){
        return tokenRepository.findByToken(token);
    }
}
