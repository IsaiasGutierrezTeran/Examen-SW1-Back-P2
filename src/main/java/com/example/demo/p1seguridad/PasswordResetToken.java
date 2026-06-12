package com.example.demo.p1seguridad;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * CU-04 — Token de un solo uso para recuperar contraseña. Colección nueva; NO
 * modifica el modelo Usuario. En prod el token se enviaría por email; aquí se
 * devuelve en la respuesta para integrarlo con el front sin servicio SMTP.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    private String id;

    private String email;
    private String token;
    private LocalDateTime expiraEn;
    private boolean usado;
}
