package com.example.demo.p3politicas;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlujoTransicionRepository extends MongoRepository<FlujoTransicion, String> {
    List<FlujoTransicion> findByDiagramaId(String diagramaId);
    List<FlujoTransicion> findByNodoOrigenId(String nodoOrigenId);
    List<FlujoTransicion> findByNodoDestinoId(String nodoDestinoId);
}
