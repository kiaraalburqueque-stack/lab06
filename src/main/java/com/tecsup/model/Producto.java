package com.tecsup.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
@Entity
public class Producto {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank @Size(max=100) @Column(nullable=false,length=100) private String nombre;
 @NotNull @DecimalMin("0.01") @Digits(integer=10,fraction=2) @Column(nullable=false,precision=12,scale=2) private BigDecimal precio;
 @NotNull @Min(0) @Column(nullable=false) private Integer stock;
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public String getNombre(){return nombre;} public void setNombre(String v){nombre=v;}
 public BigDecimal getPrecio(){return precio;} public void setPrecio(BigDecimal v){precio=v;}
 public Integer getStock(){return stock;} public void setStock(Integer v){stock=v;}
}
