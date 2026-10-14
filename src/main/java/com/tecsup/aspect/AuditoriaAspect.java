package com.tecsup.aspect;
import com.tecsup.exception.*;
import com.tecsup.model.Producto;
import com.tecsup.service.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.*;
@Aspect @Component @Order(1)
public class AuditoriaAspect {
 private final AuditoriaService auditoria; private final HttpServletRequest request;
 private final Map<String,String> usuarios=Map.of("Ricardo","ADMIN","Ana","USER","Luis","USER");
 public AuditoriaAspect(AuditoriaService a,HttpServletRequest r){auditoria=a;request=r;}
 public String obtenerUsuario(){String u=request.getHeader("Usuario");return u==null?"ANONIMO":u+" ("+usuarios.getOrDefault(u,"DESCONOCIDO")+")";}
 public void validarRol(String... roles){String u=request.getHeader("Usuario"),r=request.getHeader("Rol");if(u==null||u.isBlank()||r==null||r.isBlank())throw new UnauthorizedException("Debe enviar Usuario y Rol");if(!r.equals(usuarios.get(u)))throw new ForbiddenException("No tiene permisos");if(!Arrays.asList(roles).contains(r))throw new ForbiddenException("Acceso denegado");}
 @Around("execution(* com.tecsup.service.ProductoService.*(..))")
 public Object auditar(ProceedingJoinPoint jp)throws Throwable {
  String m=jp.getSignature().getName();
  validarRol((m.equals("guardar")||m.equals("listar"))?new String[]{"ADMIN","USER"}:new String[]{"ADMIN"});
  Object result=jp.proceed();
  String accion,detalle;
  switch(m){
   case "guardar": Producto p=(Producto)result;accion="CREAR";detalle="Se creó producto ID: "+p.getId()+", nombre="+p.getNombre()+", precio="+p.getPrecio();break;
   case "actualizar":accion="ACTUALIZAR";detalle="Se actualizó producto con ID: "+jp.getArgs()[0];break;
   case "eliminar":accion="ELIMINAR";detalle="Se eliminó producto ID: "+jp.getArgs()[0];break;
   case "listar":accion="LISTAR";detalle="Cantidad de productos: "+((List<?>)result).size();break;
   default: return result;
  }
  auditoria.registrar(accion,m,detalle,obtenerUsuario());return result;
 }
}
