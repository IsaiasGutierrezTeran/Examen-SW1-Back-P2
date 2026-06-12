package com.example.demo.config.seeders;

import com.example.demo.p1seguridad.UsuarioRepository;
import com.example.demo.p2organizacion.DepartamentoRepository;
import com.example.demo.p3politicas.Actividad;
import com.example.demo.p3politicas.ActividadRepository;
import com.example.demo.p3politicas.DiagramaWorkflow;
import com.example.demo.p3politicas.DiagramaWorkflowRepository;
import com.example.demo.p3politicas.FlujoTransicion;
import com.example.demo.p3politicas.FlujoTransicionRepository;
import com.example.demo.p3politicas.NodoDiagrama;
import com.example.demo.p3politicas.NodoDiagramaRepository;
import com.example.demo.p3politicas.PoliticaNegocioRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
@Slf4j
public class DiagramaSeeder {

    @Autowired private DiagramaWorkflowRepository diagramaRepository;
    @Autowired private NodoDiagramaRepository nodoRepository;
    @Autowired private FlujoTransicionRepository transicionRepository;
    @Autowired private PoliticaNegocioRepository politicaRepository;
    @Autowired private DepartamentoRepository departamentoRepository;
    @Autowired private ActividadRepository actividadRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    public void seed() {
        if (diagramaRepository.count() > 0) {
            log.info("[Seeder] Diagramas ya existen, se omite la creacion");
            return;
        }

        String adminId = usuarioRepository.findByEmail("admin@cre.bo")
                .map(u -> u.getId()).orElse("system");

        String politicaId = politicaRepository.findAll().stream()
                .filter(p -> "Nueva conexion residencial".equals(p.getNombre()))
                .findFirst().map(p -> p.getId()).orElse(null);

        String atcId = deptoId("ATC");
        String tecId = deptoId("TEC");
        String legId = deptoId("LEG");
        String opeId = deptoId("OPE");

        List<Actividad> acts = actividadRepository.findAll();
        String actAtcVer    = actId(acts, "Verificación de documentos del cliente");
        String actTecInsp   = actId(acts, "Inspección técnica en campo");
        String actTecPres   = actId(acts, "Elaboración de presupuesto técnico");
        String actLegContr  = actId(acts, "Revisión y aprobación del contrato");
        String actOpeCierre = actId(acts, "Ejecución de trabajos técnicos");

        DiagramaWorkflow diagrama = new DiagramaWorkflow();
        diagrama.setNombre("Flujo - Nueva Conexion Residencial");
        diagrama.setPoliticaId(politicaId);
        diagrama.setCreadorId(adminId);
        diagrama.setSwimlanes(List.of("ATC", "TEC", "LEG", "OPE"));
        diagrama.setVersionActual(1);
        diagrama.setEstado("publicado");
        diagrama.setGeneradoPorIa(false);
        diagrama.setFechaCreacion(LocalDateTime.now());
        diagrama.setUltimaModificacion(LocalDateTime.now());
        diagrama = diagramaRepository.save(diagrama);
        String diagId = diagrama.getId();

        if (politicaId != null) {
            politicaRepository.findById(politicaId).ifPresent(p -> {
                p.setDiagramaId(diagId);
                politicaRepository.save(p);
            });
        }

        NodoDiagrama nInicio    = nodo(diagId, "inicio",    "Inicio",                null,        null,  null,  1);
        NodoDiagrama nAtcVer    = nodo(diagId, "actividad", "Verificar Documentos",  actAtcVer,   atcId, "ATC", 2);
        NodoDiagrama nFork      = nodo(diagId, "fork",      "Fork",                  null,        null,  null,  3);
        NodoDiagrama nTecInsp   = nodo(diagId, "actividad", "Inspeccion en Campo",   actTecInsp,  tecId, "TEC", 4);
        NodoDiagrama nTecPres   = nodo(diagId, "actividad", "Elaborar Presupuesto",  actTecPres,  tecId, "TEC", 5);
        NodoDiagrama nJoin      = nodo(diagId, "join",      "Join",                  null,        null,  null,  6);
        NodoDiagrama nLegContr  = nodo(diagId, "actividad", "Revisar Contrato",      actLegContr, legId, "LEG", 7);
        NodoDiagrama nDecision  = nodo(diagId, "decision",  "Contrato aprobado?",    null,        null,  null,  8);
        NodoDiagrama nOpeCierre = nodo(diagId, "actividad", "Cierre y Conexion",     actOpeCierre,opeId, "OPE", 9);
        NodoDiagrama nFin       = nodo(diagId, "fin",       "Fin",                   null,        null,  null,  10);

        trans(diagId, nInicio.getId(),    nAtcVer.getId(),    "secuencial",  null,        null);
        trans(diagId, nAtcVer.getId(),    nFork.getId(),      "secuencial",  null,        null);
        trans(diagId, nFork.getId(),      nTecInsp.getId(),   "paralelo",    null,        "Rama 1");
        trans(diagId, nFork.getId(),      nTecPres.getId(),   "paralelo",    null,        "Rama 2");
        trans(diagId, nTecInsp.getId(),   nJoin.getId(),      "paralelo",    null,        null);
        trans(diagId, nTecPres.getId(),   nJoin.getId(),      "paralelo",    null,        null);
        trans(diagId, nJoin.getId(),      nLegContr.getId(),  "secuencial",  null,        null);
        trans(diagId, nLegContr.getId(),  nDecision.getId(),  "secuencial",  null,        null);
        trans(diagId, nDecision.getId(),  nOpeCierre.getId(), "condicional", "si",  "si");
        trans(diagId, nDecision.getId(),  nLegContr.getId(),  "condicional", "no",  "no");
        trans(diagId, nOpeCierre.getId(), nFin.getId(),       "secuencial",  null,        null);

        log.info("[Seeder] Diagrama Conexion Residencial OK");

        // ─────────────────────────────────────────────────────────────────
        // Segundo diagrama: FORK a DEPARTAMENTOS DISTINTOS (TEC + LEG).
        // El trámite, al llegar al fork, abre dos ramas paralelas en
        // departamentos diferentes -> aparece en la bandeja del funcionario
        // técnico (Carlos/TEC) y del legal (Pedro/LEG) a la vez, compartiendo
        // el mismo expediente y documentos.
        // ─────────────────────────────────────────────────────────────────
        String politicaConjuntaId = politicaRepository.findAll().stream()
                .filter(p -> "Inspeccion conjunta tecnico-legal".equals(p.getNombre()))
                .findFirst().map(p -> p.getId()).orElse(null);

        DiagramaWorkflow diag2 = new DiagramaWorkflow();
        diag2.setNombre("Flujo - Inspeccion Conjunta Tecnico-Legal");
        diag2.setPoliticaId(politicaConjuntaId);
        diag2.setCreadorId(adminId);
        diag2.setSwimlanes(List.of("ATC", "TEC", "LEG"));
        diag2.setVersionActual(1);
        diag2.setEstado("publicado");
        diag2.setGeneradoPorIa(false);
        diag2.setFechaCreacion(LocalDateTime.now());
        diag2.setUltimaModificacion(LocalDateTime.now());
        diag2 = diagramaRepository.save(diag2);
        final String diag2Id = diag2.getId();

        if (politicaConjuntaId != null) {
            politicaRepository.findById(politicaConjuntaId).ifPresent(p -> {
                p.setDiagramaId(diag2Id);
                politicaRepository.save(p);
            });
        }

        NodoDiagrama c_nInicio = nodo(diag2Id, "inicio",    "Inicio Conjunto",             null,        null,  null,  1);
        NodoDiagrama c_nRecep  = nodo(diag2Id, "actividad", "Recepcion Conjunta",          actAtcVer,   atcId, "ATC", 2);
        NodoDiagrama c_nFork   = nodo(diag2Id, "fork",      "Fork Conjunto",               null,        null,  null,  3);
        NodoDiagrama c_nTec    = nodo(diag2Id, "actividad", "Inspeccion Tecnica Conjunta", actTecInsp,  tecId, "TEC", 4);
        NodoDiagrama c_nLeg    = nodo(diag2Id, "actividad", "Revision Legal Conjunta",     actLegContr, legId, "LEG", 5);
        NodoDiagrama c_nJoin   = nodo(diag2Id, "join",      "Join Conjunto",               null,        null,  null,  6);
        NodoDiagrama c_nFin    = nodo(diag2Id, "fin",       "Fin Conjunto",                null,        null,  null,  7);

        trans(diag2Id, c_nInicio.getId(), c_nRecep.getId(), "secuencial", null, null);
        trans(diag2Id, c_nRecep.getId(),  c_nFork.getId(),  "secuencial", null, null);
        trans(diag2Id, c_nFork.getId(),   c_nTec.getId(),   "paralelo",   null, "Rama Tecnica");
        trans(diag2Id, c_nFork.getId(),   c_nLeg.getId(),   "paralelo",   null, "Rama Legal");
        trans(diag2Id, c_nTec.getId(),    c_nJoin.getId(),  "paralelo",   null, null);
        trans(diag2Id, c_nLeg.getId(),    c_nJoin.getId(),  "paralelo",   null, null);
        trans(diag2Id, c_nJoin.getId(),   c_nFin.getId(),   "secuencial", null, null);

        log.info("[Seeder] Diagrama Inspeccion Conjunta (fork TEC+LEG) OK");
    }

    private String deptoId(String codigo) {
        return departamentoRepository.findByCodigo(codigo).map(d -> d.getId()).orElse(null);
    }

    private String actId(List<Actividad> acts, String nombre) {
        return acts.stream().filter(a -> nombre.equals(a.getNombre()))
                .findFirst().map(Actividad::getId).orElse(null);
    }

    private NodoDiagrama nodo(String diagId, String tipo, String nombre, String actividadId,
                               String departamentoId, String swimlane, int orden) {
        NodoDiagrama n = new NodoDiagrama();
        n.setDiagramaId(diagId);
        n.setTipo(tipo);
        n.setNombre(nombre);
        n.setActividadId(actividadId);
        n.setDepartamentoId(departamentoId);
        n.setSwimlane(swimlane);
        n.setOrden(orden);
        return nodoRepository.save(n);
    }

    private void trans(String diagId, String origenId, String destinoId,
                       String tipo, String condicion, String etiqueta) {
        FlujoTransicion t = new FlujoTransicion();
        t.setDiagramaId(diagId);
        t.setNodoOrigenId(origenId);
        t.setNodoDestinoId(destinoId);
        t.setTipo(tipo);
        t.setCondicion(condicion);
        t.setEtiqueta(etiqueta);
        transicionRepository.save(t);
    }
}
