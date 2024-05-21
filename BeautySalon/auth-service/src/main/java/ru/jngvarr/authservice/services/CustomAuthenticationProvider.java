package ru.jngvarr.authservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //Ищем пользователя с указанным логином
        String requestUsername = authentication.getName();
        UserDetails userDetails;
        if (requestUsername != null) {
            userDetails = userDetailsService.loadUserByUsername(requestUsername);
        } else throw new BadCredentialsException("Пользователь с таким логином не найден");

        //Сравниваем пароли из запроса и пользователя из БД
        String requestPassword = authentication.getCredentials().toString();
        if (!passwordEncoder.matches(requestPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("Пароль не подходит к учетной записи пользователя");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
