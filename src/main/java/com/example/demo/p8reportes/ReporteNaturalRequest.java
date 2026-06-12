package com.example.demo.p8reportes;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReporteNaturalRequest {

    @NotBlank
    private String consulta;

    private String formatoExport;
}
