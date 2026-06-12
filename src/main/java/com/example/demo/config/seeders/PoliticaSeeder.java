package com.example.demo.config.seeders;

import com.example.demo.p1seguridad.UsuarioRepository;
import com.example.demo.p3politicas.PoliticaNegocio;
import com.example.demo.p3politicas.PoliticaNegocioRepository;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PoliticaSeeder {

    @Autowired private PoliticaNegocioRepository politicaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    public void seed() {
        crearPolitica(
                "Nueva conexion residencial",
                "Proceso de solicitud de nueva conexion electrica residencial",
                "conexiones", "activa");

        crearPolitica(
                "Reconexion por mora",
                "Proceso de reconexion del servicio electrico tras pago de deuda",
                "reconexiones", "activa");

        crearPolitica(
                "Cambio de titular",
                "Proceso administrativo para transferir la titularidad de un contrato de servicio",
                "administrativo", "borrador");

        // Política con FORK a departamentos DISTINTOS (TEC + LEG en paralelo):
        // el trámite cae a la vez en la bandeja del funcionario técnico y del legal,
        // que comparten el mismo expediente y sus documentos.
        crearPolitica(
                "Inspeccion conjunta tecnico-legal",
                "Inspeccion en paralelo: el area tecnica y el area legal revisan el mismo expediente al mismo tiempo y comparten documentos",
                "inspecciones", "activa");

        log.info("[Seeder] Politicas OK");
    }

    private void crearPolitica(String nombre, String descripcion, String categoria, String estado) {
        boolean existe = politicaRepository.findAll().stream()
                .anyMatch(p -> nombre.equals(p.getNombre()));
        if (!existe) {
            String adminId = usuarioRepository.findByEmail("admin@cre.bo")
                    .map(u -> u.getId()).orElse("system");

            PoliticaNegocio p = new PoliticaNegocio();
            p.setNombre(nombre);
            p.setDescripcion(descripcion);
            p.setCategoria(categoria);
            p.setCreadorId(adminId);
            p.setVersionActual(1);
            p.setEstado(estado);
            p.setFechaCreacion(LocalDateTime.now());
            if ("activa".equals(estado)) {
                p.setFechaActivacion(LocalDateTime.now());
            }
            politicaRepository.save(p);
        }
    }
}
