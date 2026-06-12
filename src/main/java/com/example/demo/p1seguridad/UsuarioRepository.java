package com.example.demo.p1seguridad;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByTipo(String tipo);

    List<Usuario> findByActivoTrue();

    boolean existsByEmail(String email);
}
