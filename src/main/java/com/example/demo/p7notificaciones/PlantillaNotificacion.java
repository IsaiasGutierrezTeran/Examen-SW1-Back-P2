package com.example.demo.p7notificaciones;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * CU-38 — Plantilla/regla de notificación configurable por el administrador.
 * Colección nueva; el cuerpo admite placeholders tipo {tramite}, {usuario}, {estado}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "plantillas_notificacion")
public class PlantillaNotificacion {

    @Id
    private String id;

    /** Código único de la plantilla, ej. "TRAMITE_DERIVADO". */
    @Indexed(unique = true)
    private String codigo;

    private String nombre;
    private String asunto;
    private String cuerpo;

    /** Evento que dispara la notificación (la "regla"), ej. "tramite.derivado". */
    private String evento;

    private Boolean activo = true;
}
