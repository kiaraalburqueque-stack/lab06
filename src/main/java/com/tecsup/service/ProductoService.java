package com.tecsup.service;
import com.tecsup.model.Producto;
import com.tecsup.repository.ProductoRepository;
import com.tecsup.exception.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @Transactional
public class ProductoService {
 private final ProductoRepository repo;
 public ProductoService(ProductoRepository repo){this.repo=repo;}
 public List<Producto> listar(){return repo.findAll();}
 public Producto guardar(Producto p){p.setId(null);return repo.save(p);}
 public Producto actualizar(Long id,Producto p){Producto actual=repo.findById(id).orElseThrow(()->new NotFoundException("Producto no encontrado: "+id));actual.setNombre(p.getNombre());actual.setPrecio(p.getPrecio());actual.setStock(p.getStock());return repo.save(actual);}
 public void eliminar(Long id){Producto p=repo.findById(id).orElseThrow(()->new NotFoundException("Producto no encontrado: "+id));repo.delete(p);}
}
