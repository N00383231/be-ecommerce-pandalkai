package com.pandalkai.ecommerce.service;

import com.pandalkai.ecommerce.dto.ProductoDTO;
import com.pandalkai.ecommerce.entity.Producto;
import com.pandalkai.ecommerce.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listar() {
        return repository.findAll();
    }

    public Producto buscar(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public Producto crear(ProductoDTO dto) {
        validar(dto);

        Producto producto = new Producto();
        asignarDatos(producto, dto);
        producto.setFechaIngreso(LocalDateTime.now());

        return repository.save(producto);
    }

    public Producto actualizar(Long id, ProductoDTO dto) {
        validar(dto);

        Producto producto = buscar(id);
        asignarDatos(producto, dto);

        return repository.save(producto);
    }

    public void eliminar(Long id) {
        Producto producto = buscar(id);
        repository.delete(producto);
    }

    private void validar(ProductoDTO dto) {
        if (dto == null ||
            dto.getNombre() == null || dto.getNombre().isBlank() ||
            dto.getCategoria() == null || dto.getCategoria().isBlank() ||
            dto.getPrecioCompra() == null ||
            dto.getPrecioVenta() == null ||
            dto.getStock() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Datos obligatorios incompletos");
        }

        if (dto.getPrecioCompra().signum() < 0 ||
            dto.getPrecioVenta().signum() < 0 ||
            dto.getStock() < 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Precio y stock no pueden ser negativos");
        }
    }

    private void asignarDatos(Producto producto, ProductoDTO dto) {
        producto.setNombre(dto.getNombre());
        producto.setCategoria(dto.getCategoria());
        producto.setPrecioCompra(dto.getPrecioCompra());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setStock(dto.getStock());
        producto.setMarca(dto.getMarca());
        producto.setCodigoBarras(dto.getCodigoBarras());
    }
}
