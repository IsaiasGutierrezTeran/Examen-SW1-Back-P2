package com.example.demo.p4tramites;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "estados_historicos")
public class EstadoHistorico {

    @Id
    private String id;

    @Indexed
    private String tramiteId;

    private String estadoAnterior;
    private String estadoNuevo;
    private String nodoAnteriorId;
    private String nodoNuevoId;
    private String actorId;
    private String motivo;

    private LocalDateTime fechaCambio;
}

