package com.example.demo.p3politicas;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

/**
 * CU-14 — Clonar versión de flujo. Controller NUEVO bajo /api/diagramas (ruta
 * /{id}/clonar, distinta a las de DiagramaWorkflowController).
 */
@RestController
@RequestMapping("/api/diagramas")
@Tag(name = "Clonar flujo", description = "CU-14 — clonar un diagrama como nueva versión")
public class ClonarFlujoController {

    @Autowired private ClonarFlujoService clonarFlujoService;

    @PostMapping("/{id}/clonar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "CU-14 · Clonar un flujo/diagrama como nueva versión (borrador)")
    public ResponseEntity<DiagramaWorkflow> clonar(@PathVariable String id, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clonarFlujoService.clonar(id, auth.getName()));
    }
}
