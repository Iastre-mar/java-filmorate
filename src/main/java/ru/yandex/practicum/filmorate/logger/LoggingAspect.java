package ru.yandex.practicum.filmorate.logger;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("@annotation(LogMethodResult)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws
            Throwable {
        String methodName = joinPoint.getSignature()
                                     .getName();
        Object[] args = joinPoint.getArgs();

        log.info("Вызов метода: {} с аргументами: {}", methodName, args);

        try {
            Object result = joinPoint.proceed();
            log.info("Метод {} успешно завершился. Результат: {}", methodName,
                     result);
            return result;
        } catch (Exception e) {
            log.error("Ошибка в методе {}: {}", methodName, e.getMessage(), e);
            throw e;
        }
    }
}