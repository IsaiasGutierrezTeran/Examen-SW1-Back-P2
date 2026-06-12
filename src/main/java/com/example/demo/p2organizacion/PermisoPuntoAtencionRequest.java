package com.example.demo.p2organizacion;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Data
public class PermisoPuntoAtencionRequest {

    @NotBlank(message = "politicaId es obligatorio")
    private String politicaId;

    @NotBlank(message = "actividadId es obligatorio")
    private String actividadId;

    @NotBlank(message = "nivelAcceso es obligatorio")
    private String nivelAcceso;

    private List<String> tiposDocumentoVisibles;
}
