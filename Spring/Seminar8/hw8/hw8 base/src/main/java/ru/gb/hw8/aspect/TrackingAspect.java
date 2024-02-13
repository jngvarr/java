package ru.gb.hw8.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Аспект Spring AOP для отслеживания действий пользователя в приложении.
 * Применяется ко всем методам в классах, помеченных аннотацией @TrackUserAction.
 * Логирует информацию о вызове методов и их результате.
 */
@Aspect
@Component
public class TrackingAspect {
    @Around("@within(ru.gb.hw8.aspect.TrackUserAction)")
    public Object trackUserAction(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // Логирование действия пользователя перед выполнением метода

        System.out.println("Действие пользователя: Метод " + methodName + " вызван с аргументами " + Arrays.toString(args));

        // Продолжаем с фактическим выполнением метода
        Object result = joinPoint.proceed();

        // Логирование действия пользователя после выполнения метода
        System.out.println("Действие пользователя: Метод " + methodName + " успешно выполнен с результатом " + result);

        // Возвращаем результат вызывающей стороне
        return result;
    }
}
