package com.tecsup.controller;
import com.tecsup.model.Producto;
import com.tecsup.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.List;
@RestController @RequestMapping("/api/productos")
public class ProductoController {
 private final ProductoService service;
 public ProductoController(ProductoService s){service=s;}
 @GetMapping public List<Producto> listar(){return service.listar();}
 @PostMapping public ResponseEntity<Producto> guardar(@Valid @RequestBody Producto p){return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(p));}
 @PutMapping("/{id}") public Producto actualizar(@PathVariable Long id,@Valid @RequestBody Producto p){return service.actualizar(id,p);}
 @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){service.eliminar(id);return ResponseEntity.noContent().build();}
}
