package com.tecsup.aspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.slf4j.*;
@Aspect @Component
public class LoggingAspect {
 private static final Logger log=LoggerFactory.getLogger(LoggingAspect.class);
 @Before("execution(* com.tecsup.service.*.*(..))") public void antes(JoinPoint jp){log.info("Ejecutando: {}",jp.getSignature().getName());}
 @AfterReturning("execution(* com.tecsup.service.*.*(..))") public void despues(JoinPoint jp){log.info("Finalizó: {}",jp.getSignature().getName());}
}
