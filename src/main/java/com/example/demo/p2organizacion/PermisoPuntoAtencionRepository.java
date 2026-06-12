package com.example.demo.p2organizacion;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermisoPuntoAtencionRepository extends MongoRepository<PermisoPuntoAtencion, String> {

    Optional<PermisoPuntoAtencion> findByPoliticaIdAndActividadId(String politicaId, String actividadId);

    List<PermisoPuntoAtencion> findByPoliticaId(String politicaId);

    List<PermisoPuntoAtencion> findByActividadId(String actividadId);
}
