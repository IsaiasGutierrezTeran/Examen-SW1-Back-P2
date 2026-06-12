package com.example.demo.config.seeders;

import com.example.demo.p1seguridad.UsuarioRepository;
import com.example.demo.p2organizacion.DepartamentoRepository;
import com.example.demo.p3politicas.NodoDiagrama;
import com.example.demo.p3politicas.NodoDiagramaRepository;
import com.example.demo.p3politicas.PoliticaNegocio;
import com.example.demo.p3politicas.PoliticaNegocioRepository;
import com.example.demo.p4tramites.Documento;
import com.example.demo.p4tramites.DocumentoArchivoService;
import com.example.demo.p4tramites.ExpedienteDigital;
import com.example.demo.p4tramites.ExpedienteDigitalRepository;
import com.example.demo.p4tramites.SeccionExpediente;
import com.example.demo.p4tramites.SeccionExpedienteRepository;
import com.example.demo.p4tramites.Tramite;
import com.example.demo.p4tramites.TramiteRepository;
import com.example.demo.p5bandeja.EstadoSeccion;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Siembra un trámite VIVO en estado PARALELO de la política
 * "Inspeccion conjunta tecnico-legal": el fork abrió dos ramas en departamentos
 * distintos (TEC y LEG), por lo que el MISMO trámite aparece a la vez en la
 * bandeja del funcionario técnico (Carlos) y del legal (Pedro). Ambos comparten
 * el mismo expediente y un documento común (documento compartido).
 *
 * Debe ejecutarse AL FINAL del seed (después de DocumentoArchivoSeeder) para no
 * disparar su guard `count() > 0`.
 */
@Component
@Slf4j
public class ForkDemoSeeder {

    @Autowired private TramiteRepository tramiteRepo;
    @Autowired private ExpedienteDigitalRepository expedienteRepo;
    @Autowired private SeccionExpedienteRepository seccionRepo;
    @Autowired private PoliticaNegocioRepository politicaRepo;
    @Autowired private NodoDiagramaRepository nodoRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private DepartamentoRepository departamentoRepo;
    @Autowired private DocumentoArchivoService docService;

    private static final String CODIGO = "TRM-FORK-001";

    public void seed() {
        if (tramiteRepo.findByCodigo(CODIGO).isPresent()) {
            log.info("[Seeder] Tramite fork demo ya existe, se omite");
            return;
        }

        PoliticaNegocio pol = politicaRepo.findAll().stream()
                .filter(p -> "Inspeccion conjunta tecnico-legal".equals(p.getNombre()))
                .findFirst().orElse(null);
        if (pol == null || pol.getDiagramaId() == null) {
            log.warn("[Seeder] fork demo: politica conjunta no encontrada, se omite");
            return;
        }

        List<NodoDiagrama> nodos = nodoRepo.findAll().stream()
                .filter(n -> pol.getDiagramaId().equals(n.getDiagramaId()))
                .toList();
        NodoDiagrama nTec = nodoPorNombre(nodos, "Inspeccion Tecnica Conjunta");
        NodoDiagrama nLeg = nodoPorNombre(nodos, "Revision Legal Conjunta");
        if (nTec == null || nLeg == null) {
            log.warn("[Seeder] fork demo: nodos TEC/LEG no encontrados, se omite");
            return;
        }

        String clienteId = userId("cliente@cre.bo");
        String funcTecId = userId("funcionario@cre.bo"); // Carlos Lima (TEC)
        String funcLegId = userId("func_leg@cre.bo");     // Pedro Gomez (LEG)
        String tecId = deptoId("TEC");
        String legId = deptoId("LEG");

        LocalDateTime now = LocalDateTime.now();

        // 1) Trámite en paralelo (ambas ramas activas).
        Tramite t = new Tramite();
        t.setCodigo(CODIGO);
        t.setClienteId(clienteId);
        t.setPoliticaId(pol.getId());
        t.setEstadoActual("En curso");
        t.setNodoActualId(null);
        t.setNodosParalellosActivos(new ArrayList<>(List.of(nTec.getId(), nLeg.getId())));
        t.setFuncionarioActualId(funcTecId);
        t.setPrioridad(2);
        t.setFechaInicio(now.minusDays(1));
        t.setFechaEstimadaCierre(now.plusDays(5));
        t = tramiteRepo.save(t);

        // 2) Expediente compartido.
        ExpedienteDigital exp = new ExpedienteDigital();
        exp.setTramiteId(t.getId());
        exp.setSeccionesIds(new ArrayList<>());
        exp.setFechaCreacion(now.minusDays(1));
        exp.setUltimaActualizacion(now);
        exp = expedienteRepo.save(exp);

        // 3) Una sección por rama, asignada a un funcionario de cada departamento.
        SeccionExpediente sTec = seccion(exp.getId(), nTec.getId(), tecId, 1, funcTecId, now);
        SeccionExpediente sLeg = seccion(exp.getId(), nLeg.getId(), legId, 2, funcLegId, now);

        exp.setSeccionesIds(new ArrayList<>(List.of(sTec.getId(), sLeg.getId())));
        expedienteRepo.save(exp);
        t.setExpedienteId(exp.getId());
        tramiteRepo.save(t);

        // 4) Documento compartido visible para ambos funcionarios del expediente.
        try {
            byte[] pdf = pdfPrueba("Informe Conjunto Compartido", CODIGO);
            docService.seedDocumento(t.getId(), pol.getId(), nTec.getActividadId(),
                    nTec.getId(), null, "Informe conjunto compartido (TEC + LEG)",
                    "PDF", pdf, "application/pdf");
        } catch (Exception e) {
            log.warn("[Seeder] fork demo: documento compartido fallo (no critico): {}", e.getMessage());
        }

        log.info("[Seeder] Fork demo OK: {} en bandeja de Carlos (TEC) y Pedro (LEG) a la vez", CODIGO);
    }

    private SeccionExpediente seccion(String expId, String nodoId, String deptoId,
                                      int orden, String funcionarioId, LocalDateTime asignacion) {
        SeccionExpediente s = new SeccionExpediente();
        s.setExpedienteId(expId);
        s.setNodoId(nodoId);
        s.setDepartamentoId(deptoId);
        s.setOrdenSeccion(orden);
        s.setEstado(EstadoSeccion.PENDIENTE_RECEPCION.getValor());
        s.setFuncionarioId(funcionarioId);
        s.setFechaAsignacion(asignacion);
        return seccionRepo.save(s);
    }

    private NodoDiagrama nodoPorNombre(List<NodoDiagrama> nodos, String nombre) {
        return nodos.stream().filter(n -> nombre.equals(n.getNombre()))
                .findFirst().orElse(null);
    }

    private String userId(String email) {
        return usuarioRepo.findByEmail(email).map(u -> u.getId()).orElse(null);
    }

    private String deptoId(String codigo) {
        return departamentoRepo.findByCodigo(codigo).map(d -> d.getId()).orElse(null);
    }

    private byte[] pdfPrueba(String nombre, String codigo) {
        String txt = nombre.replaceAll("[()\\\\]", " ");
        String pdf = "%PDF-1.4\n"
                + "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n"
                + "2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj\n"
                + "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 320 160]/Contents 4 0 R/Resources<</Font<</F1 5 0 R>>>>>>endobj\n"
                + "4 0 obj<</Length 80>>stream\nBT /F1 13 Tf 20 110 Td (" + txt + ") Tj ET\nendstream endobj\n"
                + "5 0 obj<</Type/Font/Subtype/Type1/BaseFont/Helvetica>>endobj\n"
                + "trailer<</Root 1 0 R>>\n%%EOF\n"
                + "% uid:" + codigo + "-" + nombre + "\n";
        return pdf.getBytes(StandardCharsets.ISO_8859_1);
    }
}
