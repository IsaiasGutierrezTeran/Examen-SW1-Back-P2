package com.example.demo.p3politicas;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColaboracionDiagramaRepository extends MongoRepository<ColaboracionDiagrama, String> {
    List<ColaboracionDiagrama> findByDiagramaId(String diagramaId);
    List<ColaboracionDiagrama> findByInvitadoId(String invitadoId);
    List<ColaboracionDiagrama> findByInvitadoIdAndEstado(String invitadoId, String estado);
    long countByInvitadoIdAndEstado(String invitadoId, String estado);
}
