package com.example.demo.p7notificaciones;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends MongoRepository<Notificacion, String> {
    List<Notificacion> findByDestinatarioIdAndLeidaFalse(String destinatarioId);
    List<Notificacion> findByDestinatarioIdOrderByFechaCreacionDesc(String destinatarioId);
    List<Notificacion> findByEstadoEnvioAndIntentosEnvioLessThan(String estadoEnvio, int maxIntentos);
    long countByDestinatarioIdAndLeidaFalse(String destinatarioId);
}
