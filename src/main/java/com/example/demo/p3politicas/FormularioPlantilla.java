package com.example.demo.p3politicas;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "formularios_plantilla")
public class FormularioPlantilla {

    @Id
    private String id;

    private String nodoId;
    private String nombre;

    private List<String> camposPlantillaIds;
    private boolean permiteAdjuntos;
    private boolean permiteDictadoVoz;
}

