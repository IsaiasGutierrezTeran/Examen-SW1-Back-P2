package com.example.demo.p4tramites;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoHistoricoRepository extends MongoRepository<EstadoHistorico, String> {
    List<EstadoHistorico> findByTramiteIdOrderByFechaCambioAsc(String tramiteId);
}
