package com.example.demo.p1seguridad;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * CU-04 — Recuperación de contraseña. Aditivo: reusa UsuarioRepository y
 * PasswordEncoder existentes; no toca AuthService ni el modelo Usuario.
 */
@Service
public class PasswordResetService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    /** Genera un token de un solo uso (15 min). Devuelve null si el email no existe
     *  (el controller responde igual para no revelar qué emails están registrados). */
    public String solicitar(String email) {
        Usuario u = usuarioRepository.findByEmail(email).orElse(null);
        if (u == null) return null;
        PasswordResetToken t = new PasswordResetToken();
        t.setEmail(email);
        t.setToken(UUID.randomUUID().toString().replace("-", ""));
        t.setExpiraEn(LocalDateTime.now().plusMinutes(15));
        t.setUsado(false);
        tokenRepository.save(t);
        return t.getToken();
    }

    /** Valida el token (no usado, no expirado) y fija la nueva contraseña. */
    public void resetear(String token, String nuevaPassword) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token requerido");
        }
        if (nuevaPassword == null || nuevaPassword.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        PasswordResetToken t = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));
        if (t.isUsado()) {
            throw new IllegalArgumentException("El token ya fue utilizado");
        }
        if (t.getExpiraEn().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El token expiró, solicita uno nuevo");
        }
        Usuario u = usuarioRepository.findByEmail(t.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        u.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(u);
        t.setUsado(true);
        tokenRepository.save(t);
    }
}
