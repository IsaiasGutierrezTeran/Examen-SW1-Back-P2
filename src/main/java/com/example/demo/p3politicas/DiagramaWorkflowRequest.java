package com.example.demo.p3politicas;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class DiagramaWorkflowRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String politicaId;
    private List<String> swimlanes;
    private Map<String, Object> canvasData;
}
