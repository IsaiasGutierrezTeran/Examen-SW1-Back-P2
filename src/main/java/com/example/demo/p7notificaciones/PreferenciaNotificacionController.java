package com.example.demo.p7notificaciones;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

/**
 * CU-06 — Configurar preferencias de notificación del usuario autenticado.
 * Controller NUEVO bajo /api/notificaciones (rutas distintas a NotificacionController).
 * El usuarioId se toma del JWT (auth.getName()), no del body, para no suplantar.
 */
@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Preferencias de notificación", description = "CU-06 — preferencias del usuario")
public class PreferenciaNotificacionController {

    @Autowired private PreferenciaNotificacionRepository repo;

    @GetMapping("/preferencias")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "CU-06 · Ver mis preferencias de notificación")
    public ResponseEntity<PreferenciaNotificacion> mias(Authentication auth) {
        PreferenciaNotificacion pref = repo.findByUsuarioId(auth.getName())
                .orElseGet(() -> {
                    PreferenciaNotificacion p = new PreferenciaNotificacion();
                    p.setUsuarioId(auth.getName());
                    return p; // valores por defecto (todo activado), aún sin persistir
                });
        return ResponseEntity.ok(pref);
    }

    @PutMapping("/preferencias")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "CU-06 · Actualizar mis preferencias de notificación")
    public ResponseEntity<PreferenciaNotificacion> guardar(@RequestBody PreferenciaNotificacion body,
                                                           Authentication auth) {
        PreferenciaNotificacion pref = repo.findByUsuarioId(auth.getName())
                .orElseGet(PreferenciaNotificacion::new);
        pref.setUsuarioId(auth.getName());
        if (body.getPorCorreo() != null) pref.setPorCorreo(body.getPorCorreo());
        if (body.getPorPush() != null) pref.setPorPush(body.getPorPush());
        if (body.getPorSistema() != null) pref.setPorSistema(body.getPorSistema());
        if (body.getTiposSilenciados() != null) pref.setTiposSilenciados(body.getTiposSilenciados());
        return ResponseEntity.ok(repo.save(pref));
    }
}
