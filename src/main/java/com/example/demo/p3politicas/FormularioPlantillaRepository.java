package com.example.demo.p3politicas;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormularioPlantillaRepository extends MongoRepository<FormularioPlantilla, String> {
    List<FormularioPlantilla> findByNodoId(String nodoId);
    Optional<FormularioPlantilla> findByNombre(String nombre);
}
