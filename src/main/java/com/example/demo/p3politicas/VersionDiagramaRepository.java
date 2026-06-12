package com.example.demo.p3politicas;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionDiagramaRepository extends MongoRepository<VersionDiagrama, String> {
    List<VersionDiagrama> findByDiagramaId(String diagramaId);
    boolean existsByDiagramaIdAndNumeroVersion(String diagramaId, int numeroVersion);
}
