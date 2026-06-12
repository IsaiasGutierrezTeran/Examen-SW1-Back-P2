package com.example.demo.p3politicas;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "politicas_negocio")
public class PoliticaNegocio {

    @Id
    private String id;

    @Indexed(unique = true)
    private String nombre;

    private String descripcion;
    private String categoria;
    private String diagramaId;
    private String creadorId;

    @Deprecated
    private String repositorioId;

    private int versionActual;
    private String estado;
    private Map<String, Object> parametros;

    private boolean requiereDocumentoResolucion;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActivacion;
}
