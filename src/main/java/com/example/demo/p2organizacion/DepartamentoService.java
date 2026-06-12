package com.example.demo.p2organizacion;

import com.example.demo.p1seguridad.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepartamentoService {

    @Autowired private DepartamentoRepository departamentoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    public Departamento crear(DepartamentoRequest req) {
        if (departamentoRepository.findByCodigo(req.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un departamento con el código: " + req.getCodigo());
        }
        if (departamentoRepository.findByNombre(req.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un departamento con el nombre: " + req.getNombre());
        }
        if (req.getJefeId() != null) {
            usuarioRepository.findById(req.getJefeId())
                    .orElseThrow(() -> new IllegalArgumentException("El jefeId no corresponde a ningún usuario"));
        }

        Departamento d = new Departamento();
        d.setNombre(req.getNombre());
        d.setCodigo(req.getCodigo().toUpperCase());
        d.setDescripcion(req.getDescripcion());
        d.setJefeId(req.getJefeId());
        d.setActivo(true);
        d.setFechaCreacion(LocalDateTime.now());

        return departamentoRepository.save(d);
    }

    public List<Departamento> listarTodos() {
        return departamentoRepository.findAll();
    }

    public List<Departamento> listarActivos() {
        return departamentoRepository.findByActivoTrue();
    }

    public Optional<Departamento> buscarPorId(String id) {
        return departamentoRepository.findById(id);
    }

    public Departamento actualizar(String id, DepartamentoRequest req) {
        Departamento d = departamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado"));

        departamentoRepository.findByCodigo(req.getCodigo())
                .ifPresent(existente -> {
                    if (!existente.getId().equals(id)) {
                        throw new IllegalArgumentException("El código ya lo usa otro departamento");
                    }
                });

        if (req.getJefeId() != null) {
            usuarioRepository.findById(req.getJefeId())
                    .orElseThrow(() -> new IllegalArgumentException("El jefeId no corresponde a ningún usuario"));
        }

        d.setNombre(req.getNombre());
        d.setCodigo(req.getCodigo().toUpperCase());
        d.setDescripcion(req.getDescripcion());
        d.setJefeId(req.getJefeId());

        return departamentoRepository.save(d);
    }

    public void desactivar(String id) {
        Departamento d = departamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado"));
        d.setActivo(false);
        departamentoRepository.save(d);
    }
}
