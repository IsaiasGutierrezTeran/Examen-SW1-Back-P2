package com.example.demo.p7notificaciones;

import com.example.demo.p1seguridad.Usuario;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * CU-06 — Preferencias de notificación por usuario. Colección nueva; no toca el
 * modelo Usuario ni Notificacion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "preferencias_notificacion")
public class PreferenciaNotificacion {

    @Id
    private String id;

    @Indexed(unique = true)
    private String usuarioId;

    private Boolean porCorreo = true;
    private Boolean porPush = true;
    private Boolean porSistema = true;

    /** Tipos/categorías de notificación que el usuario decidió silenciar. */
    private List<String> tiposSilenciados;
}
