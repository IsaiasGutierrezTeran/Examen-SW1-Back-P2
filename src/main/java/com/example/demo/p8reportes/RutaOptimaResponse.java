package com.example.demo.p8reportes;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaOptimaResponse {

    private List<String> rutaSugerida;
    private List<PasoOmitido> pasosOmitidos;
    private Float confianza;
    private String explicacion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasoOmitido {
        private String nodoId;
        private String motivo;
    }
}
