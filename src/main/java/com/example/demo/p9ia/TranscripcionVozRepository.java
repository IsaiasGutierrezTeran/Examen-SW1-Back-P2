package com.example.demo.p9ia;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranscripcionVozRepository extends MongoRepository<TranscripcionVoz, String> {
    List<TranscripcionVoz> findBySeccionId(String seccionId);
}
