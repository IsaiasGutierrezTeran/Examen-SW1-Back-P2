package com.example.demo.p8reportes;

import com.example.demo.p9ia.AgenteRequest;
import com.example.demo.p9ia.AgenteResponse;
import com.example.demo.p9ia.TranscripcionVoz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AiIntegrationController {

    @Autowired
    private AiIntegrationService aiIntegrationService;

    @PostMapping("/expedientes/secciones/{seccionId}/transcribir-voz")
    @PreAuthorize("hasRole('FUNCIONARIO')")
    public ResponseEntity<TranscripcionVoz> vozATexto(
            @PathVariable String seccionId,
            @RequestParam("audio") MultipartFile audio,
            Authentication authentication) {
        TranscripcionVoz res = aiIntegrationService.transcribirAudio(seccionId, audio, authentication.getName());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/agente/consultar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AgenteResponse> consultasAIAgente(
            @RequestBody AgenteRequest payload,
            Authentication authentication) {
        String rol = authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_USUARIO");
        AgenteResponse res = aiIntegrationService.consultarAgente(payload, authentication.getName(), rol);
        return ResponseEntity.ok(res);
    }
}
