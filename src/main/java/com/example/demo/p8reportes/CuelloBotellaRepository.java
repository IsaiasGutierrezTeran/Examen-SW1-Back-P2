package com.example.demo.p8reportes;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuelloBotellaRepository extends MongoRepository<CuelloBotella, String> {
    List<CuelloBotella> findByDepartamentoId(String departamentoId);
    List<CuelloBotella> findByPeriodo(String periodo);
    List<CuelloBotella> findAllByOrderByFechaDeteccionDesc();
    Optional<CuelloBotella> findByActividadIdAndPeriodo(String actividadId, String periodo);
}
