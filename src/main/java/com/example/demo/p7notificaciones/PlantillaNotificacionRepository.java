package com.example.demo.p7notificaciones;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantillaNotificacionRepository extends MongoRepository<PlantillaNotificacion, String> {

    Optional<PlantillaNotificacion> findByCodigo(String codigo);
}
