package com.complete.todayspace.global.aop;

import com.complete.todayspace.global.tracking.service.ExecutionTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class TimeTrackerAop {

    private final ExecutionTimeService executionTimeService;

    @Around("execution(* com.complete.todayspace.domain.chat.service.ChatRoomService.getChatRoom(..)) || " +
            "execution(* com.complete.todayspace.domain.product.service.ProductService.getResponseDto(..)) || " +
            "execution(* com.complete.todayspace.domain.product.service.ProductService.getMyProductList(..)) ||" +
            "execution(* com.complete.todayspace.domain.post.service.PostService.getMyPostList(..)) ||" +
            "execution(* com.complete.todayspace.domain.review.service.ReviewService.getMyReview(..)) ||" +
            "execution(* com.complete.todayspace.domain.review.service.ReviewService.getReviewByUsername(..)) ||" +
            "execution(* com.complete.todayspace.domain.wish.service.WishService.getMyWishList(..))")
    public Object logAndSaveExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        Long startTime = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        Long endTime = System.currentTimeMillis();
        Long executionTime = endTime - startTime;

        String joinPointSignature = joinPoint.getSignature().toString();
        executionTimeService.saveExecutionTime(joinPointSignature, executionTime);

        log.info("Method execution location: " + joinPointSignature + ", Execution time: " + executionTime + "ms");

        return proceed;
    }

}
