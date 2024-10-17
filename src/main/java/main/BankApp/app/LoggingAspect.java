package main.BankApp.app;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(Loggable)")
    public Object logMethodDetails(ProceedingJoinPoint joinPoint) throws Throwable  {
        Object[] args = joinPoint.getArgs();

        logger.info("Method {} called with arguments: {}", joinPoint.getSignature(), Arrays.toString(args));

        long start = System.currentTimeMillis();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception ex) {
            logger.error("Exception in method {} with arguments: {}", joinPoint.getSignature(), Arrays.toString(args), ex);
            throw ex;
        }

        long executionTime = System.currentTimeMillis() - start;

        logger.info("{} executed in {} ms", joinPoint.getSignature(), executionTime);

        logger.info("Method {} returned: {}", joinPoint.getSignature(), result);


        return result;
    }



}
