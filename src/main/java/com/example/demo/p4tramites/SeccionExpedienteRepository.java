package com.example.demo.p4tramites;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeccionExpedienteRepository extends MongoRepository<SeccionExpediente, String> {
    List<SeccionExpediente> findByExpedienteId(String expedienteId);
    List<SeccionExpediente> findByFuncionarioIdAndEstado(String funcionarioId, String estado);
    List<SeccionExpediente> findByExpedienteIdOrderByOrdenSeccionAsc(String expedienteId);
}
