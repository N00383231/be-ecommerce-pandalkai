package com.pandalkai.ecommerce.repository;

import com.pandalkai.ecommerce.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> buscarPorNombre(@Param("nombre") String nombre);

    List<Producto> buscarPorMarca(@Param("marca") String marca);

    @Query("SELECT p FROM Producto p WHERE p.categoria = :categoria")
    List<Producto> buscarPorCategoria(@Param("categoria") String categoria);
}
