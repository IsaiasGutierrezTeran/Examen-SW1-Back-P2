package com.example.demo.p3politicas;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagramaWorkflowRepository extends MongoRepository<DiagramaWorkflow, String> {
    Optional<DiagramaWorkflow> findByPoliticaId(String politicaId);

    List<DiagramaWorkflow> findAllByPoliticaId(String politicaId);
    List<DiagramaWorkflow> findByCreadorId(String creadorId);
    List<DiagramaWorkflow> findByEstado(String estado);
    List<DiagramaWorkflow> findByPoliticaIdIsNull();
}
