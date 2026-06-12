package com.example.demo.p7notificaciones;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;


/**
 * CU-38 — Configurar plantillas y reglas de notificación (CRUD, solo admin).
 * Controller/colección NUEVOS; no toca el flujo de envío de notificaciones actual.
 */
@RestController
@RequestMapping("/api/plantillas-notificacion")
@Tag(name = "Plantillas de notificación", description = "CU-38 — plantillas y reglas configurables")
public class PlantillaNotificacionController {

    @Autowired private PlantillaNotificacionRepository repo;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "CU-38 · Listar plantillas de notificación")
    public ResponseEntity<List<PlantillaNotificacion>> listar() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "CU-38 · Obtener una plantilla")
    public ResponseEntity<PlantillaNotificacion> obtener(@PathVariable String id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "CU-38 · Crear plantilla de notificación")
    public ResponseEntity<PlantillaNotificacion> crear(@RequestBody PlantillaNotificacion body) {
        if (body.getCodigo() == null || body.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El código de la plantilla es requerido");
        }
        if (repo.findByCodigo(body.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una plantilla con el código " + body.getCodigo());
        }
        body.setId(null);
        if (body.getActivo() == null) body.setActivo(true);
        return ResponseEntity.ok(repo.save(body));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "CU-38 · Actualizar plantilla de notificación")
    public ResponseEntity<PlantillaNotificacion> actualizar(@PathVariable String id,
                                                            @RequestBody PlantillaNotificacion body) {
        PlantillaNotificacion p = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plantilla no encontrada: " + id));
        p.setNombre(body.getNombre());
        p.setAsunto(body.getAsunto());
        p.setCuerpo(body.getCuerpo());
        p.setEvento(body.getEvento());
        if (body.getActivo() != null) p.setActivo(body.getActivo());
        if (body.getCodigo() != null && !body.getCodigo().isBlank()) {
            p.setCodigo(body.getCodigo());
        }
        return ResponseEntity.ok(repo.save(p));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "CU-38 · Eliminar plantilla de notificación")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
