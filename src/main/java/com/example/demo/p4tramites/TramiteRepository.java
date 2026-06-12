package com.example.demo.p4tramites;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TramiteRepository extends MongoRepository<Tramite, String> {

    Optional<Tramite> findByCodigo(String codigo);

    List<Tramite> findByEstadoActual(String estado);

    List<Tramite> findByClienteId(String clienteId);

    List<Tramite> findByClienteIdOrderByFechaInicioDesc(String clienteId);

    List<Tramite> findByNodoActualIdIn(List<String> nodoIds);

    List<Tramite> findByFuncionarioActualId(String funcionarioId);

    List<Tramite> findByPoliticaId(String politicaId);

    @Query("{ 'estadoActual': { $nin: ['Aprobado', 'Rechazado', 'Cancelado', 'Completado', 'Cancelado por el usuario'] } }")
    List<Tramite> findTramitesActivos();

    long countByEstadoActual(String estado);
}
