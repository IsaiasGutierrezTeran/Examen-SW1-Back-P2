package com.example.demo.p4tramites;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionDocumentoRepository extends MongoRepository<VersionDocumento, String> {

    List<VersionDocumento> findByDocumentoArchivoIdOrderByNumeroVersionDesc(String documentoArchivoId);

    Optional<VersionDocumento> findByDocumentoArchivoIdAndNumeroVersion(String documentoArchivoId,
                                                                        int numeroVersion);

    Optional<VersionDocumento> findFirstByDocumentoArchivoIdOrderByNumeroVersionDesc(String documentoArchivoId);

    Optional<VersionDocumento> findByDocumentoArchivoIdAndHashSha256(String documentoArchivoId, String hashSha256);
}
