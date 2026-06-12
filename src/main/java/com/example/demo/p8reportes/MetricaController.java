package com.example.demo.p8reportes;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/metricas")
public class MetricaController {

    @Autowired
    private MetricaTiempoRepository metricaRepository;

    @Autowired
    private CuelloBotellaRepository cuelloBotellaRepository;

    @GetMapping("/tramite/{tramiteId}")
    @PreAuthorize("hasAnyRole('FUNCIONARIO','ADMINISTRADOR')")
    public ResponseEntity<List<MetricaTiempo>> getMetricasPorTramite(@PathVariable String tramiteId) {
        return ResponseEntity.ok(metricaRepository.findByTramiteId(tramiteId));
    }

    @GetMapping("/cuellos-botella")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<CuelloBotella>> getCuellosBotella() {
        return ResponseEntity.ok(cuelloBotellaRepository.findAllByOrderByFechaDeteccionDesc());
    }

    @Autowired
    private MetricaYCuelloService metricaService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(metricaService.resumenDashboard());
    }
}
