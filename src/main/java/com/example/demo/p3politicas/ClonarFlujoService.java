package com.example.demo.p3politicas;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

/**
 * CU-14 — Clonar un flujo/diagrama como una nueva versión (borrador). Servicio
 * NUEVO: hace copia profunda (diagrama → nodos → formularios → campos → transiciones)
 * reusando los repositorios existentes. No modifica modelos ni servicios actuales.
 */
@Service
public class ClonarFlujoService {

    @Autowired private DiagramaWorkflowRepository diagramaRepo;
    @Autowired private NodoDiagramaRepository nodoRepo;
    @Autowired private FlujoTransicionRepository transicionRepo;
    @Autowired private FormularioPlantillaRepository formularioRepo;
    @Autowired private CampoPlantillaRepository campoRepo;

    public DiagramaWorkflow clonar(String diagramaId, String creadorId) {
        DiagramaWorkflow src = diagramaRepo.findById(diagramaId)
                .orElseThrow(() -> new IllegalArgumentException("Diagrama no encontrado: " + diagramaId));

        // 1) Nuevo diagrama: borrador y SIN política (evita romper el 1:1 política-diagrama).
        DiagramaWorkflow nuevo = new DiagramaWorkflow();
        nuevo.setNombre((src.getNombre() == null ? "Diagrama" : src.getNombre()) + " (copia)");
        nuevo.setPoliticaId(null);
        nuevo.setCreadorId(creadorId != null ? creadorId : src.getCreadorId());
        nuevo.setSwimlanes(src.getSwimlanes() == null ? null : new ArrayList<>(src.getSwimlanes()));
        nuevo.setCanvasData(src.getCanvasData() == null ? null : new LinkedHashMap<>(src.getCanvasData()));
        nuevo.setVersionActual(src.getVersionActual());
        nuevo.setEstado("borrador");
        nuevo.setGeneradoPorIa(src.isGeneradoPorIa());
        nuevo.setPromptOriginal(src.getPromptOriginal());
        nuevo.setFechaCreacion(LocalDateTime.now());
        nuevo.setUltimaModificacion(LocalDateTime.now());
        nuevo = diagramaRepo.save(nuevo);
        final String nuevoDiagramaId = nuevo.getId();

        // 2) Nodos (+ 3) formularios y campos de cada nodo). Mapa viejoNodoId → nuevoNodoId.
        Map<String, String> mapNodo = new HashMap<>();
        for (NodoDiagrama n : nodoRepo.findByDiagramaId(diagramaId)) {
            NodoDiagrama nn = new NodoDiagrama();
            nn.setDiagramaId(nuevoDiagramaId);
            nn.setTipo(n.getTipo());
            nn.setNombre(n.getNombre());
            nn.setActividadId(n.getActividadId());
            nn.setDepartamentoId(n.getDepartamentoId());
            nn.setSwimlane(n.getSwimlane());
            nn.setPosicion(n.getPosicion() == null ? null : new LinkedHashMap<>(n.getPosicion()));
            nn.setOrden(n.getOrden());
            nn.setOpcional(n.isOpcional());
            nn = nodoRepo.save(nn);
            mapNodo.put(n.getId(), nn.getId());

            for (FormularioPlantilla f : formularioRepo.findByNodoId(n.getId())) {
                FormularioPlantilla nf = new FormularioPlantilla();
                nf.setNodoId(nn.getId());
                nf.setNombre(f.getNombre());
                nf.setPermiteAdjuntos(f.isPermiteAdjuntos());
                nf.setPermiteDictadoVoz(f.isPermiteDictadoVoz());
                nf.setCamposPlantillaIds(new ArrayList<>());
                nf = formularioRepo.save(nf);

                List<String> nuevosCampos = new ArrayList<>();
                for (CampoPlantilla c : campoRepo.findByFormularioPlantillaId(f.getId())) {
                    CampoPlantilla nc = new CampoPlantilla();
                    nc.setFormularioPlantillaId(nf.getId());
                    nc.setNombre(c.getNombre());
                    nc.setEtiqueta(c.getEtiqueta());
                    nc.setTipo(c.getTipo());
                    nc.setObligatorio(c.isObligatorio());
                    nc.setOpciones(c.getOpciones() == null ? null : new ArrayList<>(c.getOpciones()));
                    nc.setValidacionRegex(c.getValidacionRegex());
                    nc.setFormula(c.getFormula());
                    nc.setOrden(c.getOrden());
                    nc = campoRepo.save(nc);
                    nuevosCampos.add(nc.getId());
                }
                nf.setCamposPlantillaIds(nuevosCampos);
                formularioRepo.save(nf);

                nn.setFormularioPlantillaId(nf.getId());
                nodoRepo.save(nn);
            }
        }

        // 4) Transiciones, remapeando origen/destino a los nodos nuevos.
        for (FlujoTransicion t : transicionRepo.findByDiagramaId(diagramaId)) {
            FlujoTransicion nt = new FlujoTransicion();
            nt.setDiagramaId(nuevoDiagramaId);
            nt.setNodoOrigenId(mapNodo.get(t.getNodoOrigenId()));
            nt.setNodoDestinoId(mapNodo.get(t.getNodoDestinoId()));
            nt.setTipo(t.getTipo());
            nt.setCondicion(t.getCondicion());
            nt.setEtiqueta(t.getEtiqueta());
            transicionRepo.save(nt);
        }

        return nuevo;
    }
}
