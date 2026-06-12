package com.example.demo.p3politicas;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodoDiagramaRepository extends MongoRepository<NodoDiagrama, String> {
    List<NodoDiagrama> findByDiagramaId(String diagramaId);
    List<NodoDiagrama> findByDiagramaIdAndTipo(String diagramaId, String tipo);
    List<NodoDiagrama> findByDepartamentoId(String departamentoId);
}
