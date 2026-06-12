package com.example.demo.p8reportes;

import java.util.Map;
import lombok.Data;

@Data
public class ReporteRequest {
    private String tipo;
    private String formato;
    private Map<String, Object> filtros;
}
