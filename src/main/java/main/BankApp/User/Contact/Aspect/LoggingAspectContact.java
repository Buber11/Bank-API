package main.BankApp.User.Contact.Aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspectContact {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspectContact.class);

    @Around("execution(* main.BankApp.User.Contact.Service.ContactServiceImpl.save(..)) || execution(* main.BankApp.User.Contact.Service.ContactServiceImpl.getAllContacts(..))")
    public Object logExecutionTimeService(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        logger.info("{} executed in {} ms", joinPoint.getSignature(), executionTime);

        return proceed;
    }

    @Around("execution(* main.BankApp.User.Contact.Controller.ContactController.save(..)) || execution(* main.BankApp.User.Contact.Controller.ContactController.getAll(..))")
    public Object logExecutionTimeController(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        logger.info("{} executed in {} ms", joinPoint.getSignature(), executionTime);

        return proceed;
    }

}
