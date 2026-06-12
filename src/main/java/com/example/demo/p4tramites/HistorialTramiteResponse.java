package com.example.demo.p4tramites;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialTramiteResponse {

    private String id;
    private String codigo;

    private String clienteId;
    private String clienteNombre;

    private String politicaId;
    private String politicaNombre;

    private String estadoActual;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaCierreReal;
}
