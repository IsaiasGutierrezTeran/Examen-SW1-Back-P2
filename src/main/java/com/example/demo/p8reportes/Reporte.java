package com.example.demo.p8reportes;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reportes")
public class Reporte {

    @Id
    private String id;

    private String generadoPorId;
    private String tipo;
    private Map<String, Object> filtros;
    private String formato;
    private String urlArchivo;

    private LocalDateTime fechaGeneracion;
}

