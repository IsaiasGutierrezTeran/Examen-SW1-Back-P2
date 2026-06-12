package com.example.demo.p3politicas;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PromptFlujoResponse {
    private DiagramaWorkflow diagrama;
    private List<NodoDiagrama> nodos;
    private List<FlujoTransicion> transiciones;
    private String promptUsado;
}
