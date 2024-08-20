package com.complete.todayspace.global.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimeTrackerAop {

    private static final Logger logger = LoggerFactory.getLogger(TimeTrackerAop.class);

    @Around("execution(* com.complete.todayspace.domain.chat.service.ChatService.getChatRoom(..)) || "
        + "execution(* com.complete.todayspace.domain.product.service.ProductService.getProductPage(..)) || "
        + "execution(* com.complete.todayspace.domain.product.service.ProductService.getMyProductList(..))")
    public Object checkTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        logger.info(joinPoint.getSignature() + " executed in " + executionTime + "ms");

        return proceed;
    }

}
