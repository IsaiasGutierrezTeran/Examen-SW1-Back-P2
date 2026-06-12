package com.example.demo.p1seguridad;

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
@Document(collection = "roles")
public class Rol {

    @Id
    private String id;

    @Indexed(unique = true)
    private String nombre;

    private String descripcion;
    private List<String> permisos;
    private boolean esSistema;

    private LocalDateTime fechaCreacion;
}
