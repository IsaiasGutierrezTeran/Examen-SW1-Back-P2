package com.example.demo.config.seeders;

import com.example.demo.models.PoliticaNegocio;
import com.example.demo.repositories.PoliticaNegocioRepository;
import com.example.demo.repositories.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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

        // Politicas de la empresa de redes y telecomunicaciones
        crearPolitica(
                "Instalacion de fibra optica al hogar (FTTH)",
                "Solicitud, inspeccion de factibilidad e instalacion de internet por fibra optica",
                "instalaciones", "activa");
        crearPolitica(
                "Contratacion de plan de internet",
                "Alta de un nuevo plan de internet/telefonia: validacion de cobertura y contrato",
                "comercial", "activa");
        crearPolitica(
                "Soporte y reparacion de averia",
                "Registro y atencion de una averia del servicio: diagnostico, visita tecnica y cierre",
                "soporte", "activa");
        crearPolitica(
                "Ampliacion de ancho de banda",
                "Upgrade del plan contratado a mayor velocidad, con revision de la red del cliente",
                "comercial", "activa");
        crearPolitica(
                "Portabilidad numerica",
                "Proceso de portabilidad del numero telefonico desde otro operador",
                "administrativo", "borrador");
        crearPolitica(
                "Baja del servicio",
                "Solicitud de cancelacion del servicio, retiro de equipos y liquidacion final",
                "administrativo", "borrador");

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
