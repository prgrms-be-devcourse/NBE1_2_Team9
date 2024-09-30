package com.grepp.nbe1_2_team09.common.util.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class LogAspect {

    Logger logger = LoggerFactory.getLogger(LogAspect.class);

    //특정 패키지 아래에 있는 모든 메서드에 AOP 적용
    /*@Pointcut("within(com.grepp.nbe1_2_team09.controller.location.LocationApiController)")
    public void controllerMethods() {}*/

    //위의 코드를 사용하려면  @Around("controllerMethods()") 추가
    //해당 코드 사용시 원하는 메소드 위에 @LogExecutionTime추가
    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 메서드 실행
        Object proceed = joinPoint.proceed();

        stopWatch.stop();
        logger.info("\n==============================\n" +
                        "Class Method: {}\n" +
                        "Execution Time: {} ms\n" +
                        "==============================",
                joinPoint.getSignature().toShortString(),  // 클래스 이름
                stopWatch.getTotalTimeMillis());           // 실행 시간

        return proceed;
    }

}
