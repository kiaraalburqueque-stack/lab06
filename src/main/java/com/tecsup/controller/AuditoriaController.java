package com.tecsup.controller;
import com.tecsup.aspect.AuditoriaAspect;
import com.tecsup.model.AuditoriaLog;
import com.tecsup.repository.AuditoriaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/auditoria")
public class AuditoriaController {
 private final AuditoriaRepository repo;private final AuditoriaAspect access;
 public AuditoriaController(AuditoriaRepository r,AuditoriaAspect a){repo=r;access=a;}
 @GetMapping public List<AuditoriaLog> listar(){access.validarRol("ADMIN");return repo.findAll(Sort.by(Sort.Direction.DESC,"id"));}
}
