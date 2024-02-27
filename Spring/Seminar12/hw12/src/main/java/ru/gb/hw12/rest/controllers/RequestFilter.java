package ru.gb.hw12.rest.controllers;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import ru.gb.hw12.services.FileGateway;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@WebFilter(urlPatterns = "/*") // Указывает, что фильтр будет применяться ко всем URL-путям ("/*").
//@Order(-999) Задает порядок выполнения фильтра. Более низкое значение -999 означает, что фильтр будет выполняться раньше остальных.
//@Slf4j Аннотация Lombok для автоматической генерации логгера.
@RequiredArgsConstructor
public class RequestFilter extends OncePerRequestFilter {

    private final FileGateway fileGateway;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
        byte[] requestBody = req.getContentAsByteArray();

        fileGateway.writeToFile("User_requests" , LocalDateTime.now() + ": " + req.getRequestURL().toString());

        filterChain.doFilter(req, response);
    }
}
