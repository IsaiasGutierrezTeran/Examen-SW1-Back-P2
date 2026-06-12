package com.example.demo.p4tramites;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoArchivoRepository extends MongoRepository<DocumentoArchivo, String> {

    List<DocumentoArchivo> findByRepositorioIdAndActivoTrue(String repositorioId);

    List<DocumentoArchivo> findByTramiteIdAndActivoTrue(String tramiteId);

    List<DocumentoArchivo> findByTramiteIdAndActividadIdAndActivoTrue(String tramiteId, String actividadId);

    List<DocumentoArchivo> findByPoliticaIdAndActivoTrue(String politicaId);
}
