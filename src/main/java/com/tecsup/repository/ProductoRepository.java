package com.tecsup.repository;
import com.tecsup.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductoRepository extends JpaRepository<Producto,Long>{}
