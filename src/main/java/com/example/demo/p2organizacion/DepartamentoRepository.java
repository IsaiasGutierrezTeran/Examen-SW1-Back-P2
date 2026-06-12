package com.example.demo.p2organizacion;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartamentoRepository extends MongoRepository<Departamento, String> {
    Optional<Departamento> findByCodigo(String codigo);
    Optional<Departamento> findByNombre(String nombre);
    List<Departamento> findByActivoTrue();
}
