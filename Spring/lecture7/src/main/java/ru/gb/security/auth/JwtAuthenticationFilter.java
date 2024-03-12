package ru.gb.security.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.annotations.Comment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component()
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter implements Filter {
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
    }

    @Override //создание токена
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserDetails principal = (UserDetails) authResult.getPrincipal();
        String token = Jwts.builder()
                .setSubject(principal.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() +
                        JwtProperties.EXPIRATION_TIME)) // Устанавливаем срок действия токена
                .signWith(SignatureAlgorithm.HS512, JwtProperties.SECRET) // Подписываем токен нашим серверным секретом
                .compact();
        response.addHeader(JwtProperties.HEADER_STRING,
                JwtProperties.TOKEN_PREFIX + token); // Добавляем токен в заголовок ответа
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        return super.attemptAuthentication(request, response);
    }
}
/// JwtProperties - это просто класс, содержащий константы, связанные с JWT, такие как строка заголовка(“Authorization”),
// префикс токена (“Bearer”),секрет и время истечения токена.
