package com.example.demo.config.seeders;

import com.example.demo.p3politicas.Actividad;
import com.example.demo.p3politicas.ActividadRepository;
import com.example.demo.p3politicas.NodoDiagrama;
import com.example.demo.p3politicas.NodoDiagramaRepository;
import com.example.demo.p3politicas.PoliticaNegocioRepository;
import com.example.demo.p4tramites.Documento;
import com.example.demo.p4tramites.DocumentoArchivo;
import com.example.demo.p4tramites.DocumentoArchivoRepository;
import com.example.demo.p4tramites.DocumentoArchivoService;
import com.example.demo.p4tramites.DocumentoRepository;
import com.example.demo.p4tramites.RequisitoDocumento;
import com.example.demo.p4tramites.RequisitoDocumentoService;
import com.example.demo.p4tramites.SeccionExpediente;
import com.example.demo.p4tramites.SeccionExpedienteRepository;
import com.example.demo.p4tramites.Tramite;
import com.example.demo.p4tramites.TramiteRepository;
import com.example.demo.p5bandeja.EstadoSeccion;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DocumentoArchivoSeeder {

    @Autowired private TramiteRepository tramiteRepo;
    @Autowired private NodoDiagramaRepository nodoRepo;
    @Autowired private ActividadRepository actividadRepo;
    @Autowired private DocumentoRepository documentoRepo;
    @Autowired private PoliticaNegocioRepository politicaRepo;
    @Autowired private DocumentoArchivoRepository docArchivoRepo;
    @Autowired private SeccionExpedienteRepository seccionRepo;
    @Autowired private RequisitoDocumentoService requisitoService;
    @Autowired private DocumentoArchivoService docService;

    @Value("${aws.enabled:false}") private boolean s3Enabled;

    public void seed() {
        if (docArchivoRepo.count() > 0) {
            log.info("[Seeder] DocumentoArchivo ya existen, se omite");
            return;
        }
        if (!s3Enabled) {
            log.info("[Seeder] DocumentoArchivo omitido (S3 deshabilitado)");
            return;
        }

        int creados = 0;

        for (Tramite t : tramiteRepo.findAll()) {
            if (t.getExpedienteId() == null) continue;
            Set<String> yaSembrados = new HashSet<>();
            for (SeccionExpediente s : seccionRepo.findByExpedienteIdOrderByOrdenSeccionAsc(t.getExpedienteId())) {
                EstadoSeccion es = EstadoSeccion.from(s.getEstado());
                if (es == null || es == EstadoSeccion.BLOQUEADA || es == EstadoSeccion.PENDIENTE_DOCUMENTOS) continue;
                NodoDiagrama nodo = nodoRepo.findById(s.getNodoId()).orElse(null);
                if (nodo == null || nodo.getActividadId() == null) continue;
                Actividad act = actividadRepo.findById(nodo.getActividadId()).orElse(null);
                if (act == null) continue;
                for (RequisitoDocumento req : requisitoService.requisitosObligatoriosCliente(act)) {
                    if (req.getDocumentoId() == null || !yaSembrados.add(req.getDocumentoId())) continue;
                    Documento tipo = documentoRepo.findById(req.getDocumentoId()).orElse(null);
                    String nombre = tipo != null ? tipo.getNombre() : "Documento";
                    try {
                        docService.seedDocumento(t.getId(), t.getPoliticaId(), nodo.getActividadId(),
                                s.getNodoId(), req.getDocumentoId(), nombre, "PDF",
                                pdfPrueba(nombre, t.getCodigo()), "application/pdf");
                        creados++;
                    } catch (Exception e) {
                        log.warn("[Seeder] doc '{}' tramite {} fallo: {}", nombre, t.getCodigo(), e.getMessage());
                    }
                }
            }
        }
        log.info("[Seeder] DocumentoArchivo OK ({} documentos segun progreso)", creados);

        int marcados = 0;
        for (Tramite t : tramiteRepo.findAll()) {
            if (t.getExpedienteId() == null) continue;
            for (SeccionExpediente s : seccionRepo.findByExpedienteIdOrderByOrdenSeccionAsc(t.getExpedienteId())) {
                if (EstadoSeccion.from(s.getEstado()) != EstadoSeccion.OBSERVADO) continue;
                if (s.getDocumentosObservados() != null && !s.getDocumentosObservados().isEmpty()) continue;
                NodoDiagrama nodo = nodoRepo.findById(s.getNodoId()).orElse(null);
                if (nodo == null || nodo.getActividadId() == null) continue;
                List<DocumentoArchivo> docs = docArchivoRepo
                        .findByTramiteIdAndActividadIdAndActivoTrue(t.getId(), nodo.getActividadId());
                String docId = docs.isEmpty() ? null : docs.get(0).getId();
                if (docId == null) {
                    Actividad act = actividadRepo.findById(nodo.getActividadId()).orElse(null);
                    List<RequisitoDocumento> reqs = requisitoService.requisitosObligatoriosCliente(act);
                    if (!reqs.isEmpty()) {
                        RequisitoDocumento req = reqs.get(0);
                        Documento tipo = documentoRepo.findById(req.getDocumentoId()).orElse(null);
                        String nombre = tipo != null ? tipo.getNombre() : "Documento";
                        try {
                            docId = docService.seedDocumento(t.getId(), t.getPoliticaId(), nodo.getActividadId(),
                                    s.getNodoId(), req.getDocumentoId(), nombre, "PDF",
                                    pdfPrueba(nombre, t.getCodigo() + "-obs"), "application/pdf");
                        } catch (Exception e) {
                            log.warn("[Seeder] observado tramite {} fallo: {}", t.getCodigo(), e.getMessage());
                        }
                    }
                }
                if (docId != null) {
                    s.setDocumentosObservados(new ArrayList<>(List.of(docId)));
                    seccionRepo.save(s);
                    marcados++;
                }
            }
        }
        log.info("[Seeder] Secciones OBSERVADO marcadas: {}", marcados);
    }

    private byte[] pdfPrueba(String nombre, String codigo) {
        String txt = sanitizar(nombre);
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

    private String sanitizar(String s) {
        return s == null ? "Documento" : s.replaceAll("[()\\\\]", " ");
    }
}
