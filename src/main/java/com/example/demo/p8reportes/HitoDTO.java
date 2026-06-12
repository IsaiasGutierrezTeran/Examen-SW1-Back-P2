package com.example.demo.p8reportes;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HitoDTO {
    private LocalDateTime fecha;
    private String estado;
    private String departamento;
    private String actor;
    private boolean esActual;
}
