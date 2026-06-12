package com.example.demo.p3politicas;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.Data;

@Data
public class PoliticaNegocioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;
    private String categoria;
    private String diagramaId;
    private Map<String, Object> parametros;

    private boolean requiereDocumentoResolucion;
}
