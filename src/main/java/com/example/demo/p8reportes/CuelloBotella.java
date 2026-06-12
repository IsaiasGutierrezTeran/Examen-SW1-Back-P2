package com.example.demo.p8reportes;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cuellos_botella")
public class CuelloBotella {

    @Id
    private String id;

    private String actividadId;
    private String departamentoId;

    private String periodo;
    private int tramitesAcumulados;
    private float tiempoPromedio;
    private float tiempoEsperado;
    private float desviacionPorcentaje;
    private String causaSugerida;

    private LocalDateTime fechaDeteccion;
}

