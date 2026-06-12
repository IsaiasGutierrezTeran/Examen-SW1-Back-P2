package com.example.demo.p3politicas;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionPoliticaRepository extends MongoRepository<VersionPolitica, String> {
    List<VersionPolitica> findByPoliticaId(String politicaId);
    boolean existsByPoliticaIdAndNumeroVersion(String politicaId, int numeroVersion);
}
