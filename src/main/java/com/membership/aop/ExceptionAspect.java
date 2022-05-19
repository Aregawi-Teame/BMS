package com.membership.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionAspect {
    @AfterThrowing(value = "execution(public * com.membership.service.*.*(..))", throwing = "exception")
    public void handleServiceExceptions(JoinPoint joinpoint, Exception exception) {
        System.out.println("Method: " + joinpoint.getSignature().getName());
        System.out.println("Exception message: " + exception.getMessage());
    }
}
