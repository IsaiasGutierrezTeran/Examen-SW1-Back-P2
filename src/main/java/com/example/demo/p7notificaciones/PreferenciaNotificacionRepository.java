package com.example.demo.p7notificaciones;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenciaNotificacionRepository extends MongoRepository<PreferenciaNotificacion, String> {

    Optional<PreferenciaNotificacion> findByUsuarioId(String usuarioId);
}
