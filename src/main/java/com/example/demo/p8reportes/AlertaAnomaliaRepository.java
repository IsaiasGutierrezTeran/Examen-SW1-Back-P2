package com.example.demo.p8reportes;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaAnomaliaRepository extends MongoRepository<AlertaAnomalia, String> {

    List<AlertaAnomalia> findByTramiteIdOrderByFechaDeteccionDesc(String tramiteId);

    List<AlertaAnomalia> findByFalsoPositivoFalseOrderByFechaDeteccionDesc();

    // Para deduplicar: ya existe una alerta de esta categoria para el tramite.
    List<AlertaAnomalia> findByTramiteIdAndCategoria(String tramiteId, String categoria);
}
