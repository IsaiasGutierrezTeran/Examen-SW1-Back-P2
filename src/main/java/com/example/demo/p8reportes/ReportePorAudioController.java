package com.example.demo.p8reportes;

import com.example.demo.p9ia.IaProxyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.*;


/**
 * CU-47 — Generar reporte dinámico por AUDIO. Controller NUEVO que encadena
 * transcripción (audio→texto, IaProxyService) con el reporte en lenguaje natural
 * (ReporteNaturalService). No modifica esos servicios.
 */
@RestController
@RequestMapping("/api/reportes")
@Tag(name = "IA · Reportes por audio", description = "CU-47 — reporte dinámico dictado por voz")
public class ReportePorAudioController {

    @Autowired private IaProxyService iaProxy;
    @Autowired private ReporteNaturalService reporteNaturalService;

    @PostMapping(value = "/consulta-por-audio", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "CU-47 · Generar reporte por audio (audio → texto → reporte)")
    public ResponseEntity<ReporteNaturalResponse> generarPorAudio(
            @RequestParam("audio") MultipartFile audio,
            Authentication auth) {

        Map<String, Object> tr = iaProxy.vozAFormulario(audio, "[]");
        Object texto = tr.get("texto_transcrito");
        if (texto == null || texto.toString().isBlank()) {
            throw new IllegalArgumentException("No se pudo transcribir el audio.");
        }

        ReporteNaturalRequest req = new ReporteNaturalRequest();
        req.setConsulta(texto.toString());
        return ResponseEntity.ok(reporteNaturalService.generar(req, auth.getName()));
    }
}
