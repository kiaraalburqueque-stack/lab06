package com.tecsup.aspect;
import com.tecsup.service.AuditoriaService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.slf4j.*;
@Aspect @Component @Order(0)
public class ErrorAspect {
 private static final Logger log=LoggerFactory.getLogger(ErrorAspect.class);
 private final AuditoriaService auditoria;private final AuditoriaAspect identity;
 public ErrorAspect(AuditoriaService a,AuditoriaAspect i){auditoria=a;identity=i;}
 @AfterThrowing(pointcut="execution(* com.tecsup.service.ProductoService.*(..))",throwing="ex")
 public void capturarError(JoinPoint jp,Exception ex){log.warn("ERROR AOP: {}",ex.getMessage());String detalle=ex.getClass().getSimpleName()+": "+ex.getMessage();try{auditoria.registrar("ERROR",jp.getSignature().getName(),detalle.substring(0,Math.min(2000,detalle.length())),identity.obtenerUsuario());}catch(Exception failure){log.error("No se pudo registrar el error en auditoría",failure);}}
}
