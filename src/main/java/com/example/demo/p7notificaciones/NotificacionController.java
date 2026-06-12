package com.example.demo.p7notificaciones;

import com.example.demo.p6monitoreo.SseService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private SseService sseService;

    @GetMapping("/mis-notificaciones")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notificacion>> getMisNotificaciones(Authentication authentication) {
        List<Notificacion> notificaciones = notificacionRepository
                .findByDestinatarioIdOrderByFechaCreacionDesc(authentication.getName());
        return ResponseEntity.ok(notificaciones);
    }

    @PutMapping("/{id}/marcar-leida")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Notificacion> marcarLeida(@PathVariable String id, Authentication authentication) {
        Notificacion n = notificacionService.marcarComoLeida(id, authentication.getName());
        return ResponseEntity.ok(n);
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("isAuthenticated()")
    public SseEmitter stream(Authentication authentication) {
        return sseService.abrirStream(authentication.getName());
    }
}
