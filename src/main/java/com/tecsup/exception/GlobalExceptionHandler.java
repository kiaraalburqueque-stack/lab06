package com.tecsup.exception;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<Map<String,String>> invalid(MethodArgumentNotValidException ex){Map<String,String> errors=new LinkedHashMap<>();ex.getBindingResult().getFieldErrors().forEach(e->errors.put(e.getField(),e.getDefaultMessage()));return ResponseEntity.badRequest().body(errors);}
 @ExceptionHandler(UnauthorizedException.class) public ResponseEntity<?> unauthorized(UnauthorizedException e){return error(401,e);}
 @ExceptionHandler(ForbiddenException.class) public ResponseEntity<?> forbidden(ForbiddenException e){return error(403,e);}
 @ExceptionHandler(NotFoundException.class) public ResponseEntity<?> missing(NotFoundException e){return error(404,e);}
 @ExceptionHandler(HttpMessageNotReadableException.class) public ResponseEntity<?> malformed(){return ResponseEntity.badRequest().body(Map.of("error","JSON inválido o tipos de datos incorrectos"));}
 private ResponseEntity<?> error(int status,Exception e){return ResponseEntity.status(status).body(Map.of("error",e.getMessage()));}
}
