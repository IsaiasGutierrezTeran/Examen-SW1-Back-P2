package com.example.demo.p3politicas;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoliticaNegocioRepository extends MongoRepository<PoliticaNegocio, String> {
    Optional<PoliticaNegocio> findByNombre(String nombre);
    List<PoliticaNegocio> findByEstado(String estado);
    List<PoliticaNegocio> findByCreadorId(String creadorId);
}
