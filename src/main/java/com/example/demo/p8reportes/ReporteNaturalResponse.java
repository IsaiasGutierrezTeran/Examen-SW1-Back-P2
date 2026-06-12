package com.example.demo.p8reportes;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteNaturalResponse {

    private String reporteId;
    private String collection;
    private List<Map<String, Object>> filasMuestra;
    private long totalFilas;
    private String urlDescarga;
    private String formato;

    private String queryGenerada;
}
