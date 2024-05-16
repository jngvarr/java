package ru.jngvarr.clientmanagement.config;

import dao.entities.people.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.jngvarr.clientmanagement.repositories.UserRepository;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //Ищем пользователя с указанным адресом электронной почты
        String requestUsername = authentication.getName();
        User user;
        if (requestUsername != null) {
            user = userRepository.getUserByUserName(requestUsername);
        } else throw new BadCredentialsException("Пользователь с таким логином не найден");

        //Сравниваем пароли из запроса и пользователя из БД
        String requestPassword = authentication.getCredentials().toString();
        if (!passwordEncoder.matches(requestPassword, user.getPassword())) {
            throw new BadCredentialsException("Пароль не подходит к учетной записи пользователя");
        }
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), Collections.emptyList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
