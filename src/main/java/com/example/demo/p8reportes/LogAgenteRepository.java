package com.example.demo.p8reportes;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogAgenteRepository extends MongoRepository<LogAgente, String> {
    List<LogAgente> findByUsuarioIdOrderByTimestampDesc(String usuarioId);
    List<LogAgente> findByContextoModulo(String contextoModulo);
}
