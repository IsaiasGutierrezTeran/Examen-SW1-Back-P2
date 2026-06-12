package com.example.demo.p3politicas;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampoPlantillaRepository extends MongoRepository<CampoPlantilla, String> {
    List<CampoPlantilla> findByFormularioPlantillaId(String formularioPlantillaId);
    long countByFormularioPlantillaId(String formularioPlantillaId);
}
