package com.example.demo.p8reportes;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrazabilidadRepository extends MongoRepository<Trazabilidad, String> {
    List<Trazabilidad> findByTramiteIdOrderByTimestampDesc(String tramiteId);
    List<Trazabilidad> findByTramiteIdOrderByTimestampAsc(String tramiteId);
    Trazabilidad findFirstByTramiteIdOrderByTimestampDesc(String tramiteId);
    Trazabilidad findTopByTramiteIdOrderByTimestampDesc(String tramiteId);

    List<Trazabilidad> findByTramiteIdOrderByTimestampAscIdAsc(String tramiteId);
    Trazabilidad findTopByTramiteIdOrderByTimestampDescIdDesc(String tramiteId);
}
