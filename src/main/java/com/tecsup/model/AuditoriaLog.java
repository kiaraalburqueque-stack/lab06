package com.tecsup.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="auditoria_log")
public class AuditoriaLog {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String accion; private String metodo; private LocalDateTime fecha;
 @Column(length=2000) private String detalle;
 @Column(length=100) private String usuario;
 public AuditoriaLog(){}
 public AuditoriaLog(String a,String m,String d,String u){accion=a;metodo=m;detalle=d;usuario=u;fecha=LocalDateTime.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public String getAccion(){return accion;} public void setAccion(String v){accion=v;}
 public String getMetodo(){return metodo;} public void setMetodo(String v){metodo=v;}
 public LocalDateTime getFecha(){return fecha;} public void setFecha(LocalDateTime v){fecha=v;}
 public String getDetalle(){return detalle;} public void setDetalle(String v){detalle=v;}
 public String getUsuario(){return usuario;} public void setUsuario(String v){usuario=v;}
}
