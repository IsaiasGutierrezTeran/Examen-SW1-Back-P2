package com.example.demo.p9ia;

import com.example.demo.p3politicas.PromptFlowService;
import com.example.demo.p3politicas.PromptFlujoRequest;
import com.example.demo.p3politicas.PromptFlujoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.*;


/**
 * CU-43 — Crear flujo de trámite por VOZ. Controller NUEVO que encadena dos
 * piezas existentes: transcripción (audio→texto, IaProxyService) y generación
 * de diagrama desde prompt (PromptFlowService). No toca esos servicios.
 */
@RestController
@RequestMapping("/api/workflow-design")
@Tag(name = "Diseño por IA (voz)", description = "CU-43 — crear flujo de trámite dictando por voz")
public class FlujoPorVozController {

    @Autowired private IaProxyService iaProxy;
    @Autowired private PromptFlowService promptFlowService;

    @PostMapping(value = "/from-voz", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "CU-43 · Crear flujo de trámite por voz (audio → texto → diagrama)")
    public ResponseEntity<PromptFlujoResponse> generarPorVoz(
            @RequestParam("audio") MultipartFile audio,
            @RequestParam(value = "nombreDiagrama", required = false) String nombreDiagrama,
            @RequestParam(value = "politicaId", required = false) String politicaId,
            Authentication auth) {

        Map<String, Object> tr = iaProxy.vozAFormulario(audio, "[]");
        Object texto = tr.get("texto_transcrito");
        if (texto == null || texto.toString().isBlank()) {
            throw new IllegalArgumentException("No se pudo transcribir el audio.");
        }

        PromptFlujoRequest req = new PromptFlujoRequest();
        req.setPrompt(texto.toString());
        req.setNombreDiagrama(
                (nombreDiagrama == null || nombreDiagrama.isBlank()) ? "Flujo dictado por voz" : nombreDiagrama);
        req.setPoliticaId(politicaId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(promptFlowService.generarDesdePrompt(req, auth.getName()));
    }
}
