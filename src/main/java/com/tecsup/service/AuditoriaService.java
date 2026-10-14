package com.tecsup.service;
import com.tecsup.model.AuditoriaLog;
import com.tecsup.repository.AuditoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
@Service
public class AuditoriaService {
 private final AuditoriaRepository repo;
 public AuditoriaService(AuditoriaRepository repo){this.repo=repo;}
 @Transactional(propagation=Propagation.REQUIRES_NEW)
 public void registrar(String accion,String metodo,String detalle,String usuario){repo.save(new AuditoriaLog(accion,metodo,detalle,usuario));}
}
