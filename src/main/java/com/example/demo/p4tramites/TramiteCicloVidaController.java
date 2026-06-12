package com.example.demo.p4tramites;

import com.example.demo.p8reportes.LineaTiempoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tramites/{id}")
public class TramiteCicloVidaController {

    @Autowired
    private TramiteCicloVidaService cicloVidaService;

    @PostMapping("/cancelar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Tramite> cancelarTramite(@PathVariable String id, Authentication authentication) {
        Tramite cancelado = cicloVidaService.cancelarTramite(id, authentication.getName());
        return ResponseEntity.ok(cancelado);
    }

    @GetMapping("/linea-tiempo")
    @PreAuthorize("hasAnyRole('CLIENTE','FUNCIONARIO','ADMINISTRADOR')")
    public ResponseEntity<LineaTiempoResponse> getLineaTiempo(@PathVariable String id) {
        return ResponseEntity.ok(cicloVidaService.getLineaTiempo(id));
    }
}
