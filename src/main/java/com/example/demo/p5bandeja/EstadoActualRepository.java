package com.example.demo.p5bandeja;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoActualRepository extends MongoRepository<EstadoActual, String> {
    Optional<EstadoActual> findByTramiteId(String tramiteId);
    boolean existsByTramiteId(String tramiteId);
}
