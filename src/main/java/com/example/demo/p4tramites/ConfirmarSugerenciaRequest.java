package com.example.demo.p4tramites;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmarSugerenciaRequest {

    @NotBlank(message = "politicaConfirmadaId es obligatorio")
    private String politicaConfirmadaId;
}
