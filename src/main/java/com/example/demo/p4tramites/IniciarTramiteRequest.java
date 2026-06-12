package com.example.demo.p4tramites;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IniciarTramiteRequest {

    @NotBlank(message = "clienteId es obligatorio")
    private String clienteId;

    @NotBlank(message = "politicaId es obligatorio")
    private String politicaId;

    private int prioridad;
}
