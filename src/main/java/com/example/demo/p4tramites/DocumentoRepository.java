package com.example.demo.p4tramites;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoRepository extends MongoRepository<Documento, String> {
    List<Documento> findByActivo(boolean activo);
    Optional<Documento> findByNombre(String nombre);
}
