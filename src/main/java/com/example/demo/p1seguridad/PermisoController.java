package com.example.demo.p1seguridad;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/permisos")
@Tag(name = "Permisos", description = "Catálogo de permisos del sistema (definidos en el seed)")
public class PermisoController {

    @Autowired private PermisoRepository permisoRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar permisos disponibles",
               description = "Filtro opcional: modulo=usuarios|tramites|reportes|...")
    public ResponseEntity<List<Permiso>> listar(@RequestParam(required = false) String modulo) {
        if (modulo != null) {
            return ResponseEntity.ok(permisoRepository.findByModulo(modulo));
        }
        return ResponseEntity.ok(permisoRepository.findAll());
    }
}
