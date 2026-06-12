package com.example.demo.p4tramites;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "expedientes")
public class ExpedienteDigital {

    @Id
    private String id;

    @Indexed
    private String tramiteId;

    private List<String> seccionesIds;

    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimaActualizacion;
}

