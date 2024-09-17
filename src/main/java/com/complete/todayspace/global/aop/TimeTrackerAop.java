package com.complete.todayspace.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TimeTrackerAop {

    @Around("execution(* com.complete.todayspace.domain.chat.service.ChatRoomService.getChatRoom(..)) || " +
            "execution(* com.complete.todayspace.domain.product.service.ProductService.getResponseDto(..)) || " +
            "execution(* com.complete.todayspace.domain.product.service.ProductService.getMyProductList(..)) ||" +
            "execution(* com.complete.todayspace.domain.post.service.PostService.getMyPostList(..)) ||" +
            "execution(* com.complete.todayspace.domain.review.service.ReviewService.getMyReview(..)) ||" +
            "execution(* com.complete.todayspace.domain.review.service.ReviewService.getReviewByUsername(..)) ||" +
            "execution(* com.complete.todayspace.domain.wish.service.WishService.getMyWishList(..))")
    public Object checkTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        log.info("Method execution location: " + joinPoint.getSignature() + ", Execution time: " + executionTime + "ms");

        return proceed;
    }

}
