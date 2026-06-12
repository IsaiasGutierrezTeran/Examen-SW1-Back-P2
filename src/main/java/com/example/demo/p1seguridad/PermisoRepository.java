package com.example.demo.p1seguridad;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermisoRepository extends MongoRepository<Permiso, String> {
    Optional<Permiso> findByCodigo(String codigo);
    List<Permiso> findByModulo(String modulo);
}
