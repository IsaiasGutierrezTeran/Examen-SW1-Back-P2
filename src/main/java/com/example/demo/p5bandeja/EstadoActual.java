package com.example.demo.p5bandeja;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "estados_actuales")
public class EstadoActual {

    @Id
    private String id;

    private String tramiteId;
    private String estado;
    private String nodoId;

    private LocalDateTime desde;
}

