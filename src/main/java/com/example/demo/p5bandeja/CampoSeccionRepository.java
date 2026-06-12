package com.example.demo.p5bandeja;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampoSeccionRepository extends MongoRepository<CampoSeccion, String> {
    List<CampoSeccion> findBySeccionId(String seccionId);
}
