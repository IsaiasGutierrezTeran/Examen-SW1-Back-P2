package com.example.demo.p1seguridad;

import com.example.demo.config.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


/**
 * Controller NUEVO (no toca AuthController). Cae bajo /api/auth/** que ya es
 * público en SecurityConfig, así que no requiere cambios de seguridad.
 *   CU-04 · Recuperar contraseña (solicitar + reset por token)
 *   CU-03 · Cerrar sesión (JWT stateless: el cliente descarta el token)
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Recuperación de cuenta", description = "CU-04 recuperar contraseña · CU-03 cerrar sesión")
public class RecuperacionController {

    @Autowired private PasswordResetService passwordResetService;

    @PostMapping("/password/solicitar")
    @Operation(summary = "CU-04 · Solicitar recuperación de contraseña (genera token)")
    public ResponseEntity<Map<String, Object>> solicitar(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El email es requerido"));
        }
        String token = passwordResetService.solicitar(email);
        // Siempre 200 (no revelar si el email existe). Si existe, se incluye el token.
        Map<String, Object> resp = new HashMap<>();
        resp.put("mensaje", "Si el email está registrado, se generó un token de recuperación (válido 15 min).");
        if (token != null) {
            resp.put("token", token);
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/password/reset")
    @Operation(summary = "CU-04 · Restablecer contraseña con el token recibido")
    public ResponseEntity<Map<String, String>> reset(@RequestBody Map<String, String> body) {
        passwordResetService.resetear(body.get("token"), body.get("password"));
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada. Ya puedes iniciar sesión."));
    }

    @PostMapping("/logout")
    @Operation(summary = "CU-03 · Cerrar sesión (el cliente descarta el JWT)")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada. Descarta el token en el cliente."));
    }
}
