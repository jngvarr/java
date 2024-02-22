package ru.gb.web_client.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("@annotation(LogAction)")
    public Object logAction(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Action logged: " + joinPoint.getSignature().getName());
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        System.out.println(joinPoint.getSignature().getName() + " executed in " + (endTime - startTime) + " ms");
        return result;
    }
}