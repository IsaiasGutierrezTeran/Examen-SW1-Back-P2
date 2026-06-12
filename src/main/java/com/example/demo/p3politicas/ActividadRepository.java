package com.example.demo.p3politicas;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActividadRepository extends MongoRepository<Actividad, String> {
    List<Actividad> findByDepartamentoId(String departamentoId);
    List<Actividad> findByReutilizableTrue();
}
