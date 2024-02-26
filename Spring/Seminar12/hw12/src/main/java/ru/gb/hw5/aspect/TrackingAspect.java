package ru.gb.hw5.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.gb.hw5.services.FileGateway;

import java.util.Arrays;

/**
 * Аспект Spring AOP для отслеживания действий пользователя в приложении.
 * Применяется ко всем методам в классах, помеченных аннотацией @TrackUserAction.
 * Логирует информацию о вызове методов и их результате.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class TrackingAspect {
    private final FileGateway fileGateway;
    @Around("@within(ru.gb.hw12.aspect.TrackUserRequest)")
    public Object trackUserRequest(ProceedingJoinPoint joinPoint, HttpServletRequest request) throws Throwable {
        fileGateway.writeToFile(request.getLocalName(), request.getQueryString());
        return joinPoint.proceed();
    }
}
