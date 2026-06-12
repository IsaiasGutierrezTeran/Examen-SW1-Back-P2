package com.example.demo.p4tramites;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioDocumentalRepository extends MongoRepository<RepositorioDocumental, String> {
    Optional<RepositorioDocumental> findByTramiteId(String tramiteId);
}
