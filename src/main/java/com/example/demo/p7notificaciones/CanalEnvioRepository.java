package com.example.demo.p7notificaciones;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanalEnvioRepository extends MongoRepository<CanalEnvio, String> {
    Optional<CanalEnvio> findByTipo(String tipo);
    boolean existsByTipo(String tipo);
}
