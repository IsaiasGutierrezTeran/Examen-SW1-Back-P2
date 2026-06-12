package com.example.demo.p4tramites;

import com.example.demo.p1seguridad.UsuarioRepository;
import com.example.demo.p3politicas.VersionadoService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Integración con OnlyOffice Document Server para abrir y CO-EDITAR en vivo los
 * documentos Office (.docx/.xlsx/.pptx) del repositorio del trámite. El servidor
 * de documentos descarga el original (URL prefirmada de S3), gestiona la co-edición
 * simultánea entre varios funcionarios y, al guardar, llama al callback, que crea
 * una NUEVA VERSIÓN (CU-35) en S3.
 */
@Service
@Slf4j
public class OnlyOfficeService {

    @Autowired private DocumentoArchivoRepository docRepo;
    @Autowired private VersionDocumentoRepository versionRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private S3StorageService s3;
    @Autowired private VersionadoService versionadoService;

    @Value("${onlyoffice.enabled:false}")        private boolean enabled;
    @Value("${onlyoffice.server-url:}")          private String serverUrl;     // público (navegador)
    @Value("${onlyoffice.callback-base:http://backend:8080}") private String callbackBase; // interno
    @Value("${onlyoffice.internal-url:http://onlyoffice}")    private String internalUrl;   // backend → DS (CommandService)
    @Value("${onlyoffice.jwt-secret:}")          private String jwtSecret;

    /** ext → tipo de editor de OnlyOffice. Solo estas extensiones son editables. */
    private static final Map<String, String> DOC_TYPE = Map.ofEntries(
            Map.entry("docx", "word"),  Map.entry("doc", "word"),  Map.entry("odt", "word"),
            Map.entry("rtf", "word"),   Map.entry("txt", "word"),
            Map.entry("xlsx", "cell"),  Map.entry("xls", "cell"),  Map.entry("ods", "cell"),
            Map.entry("csv", "cell"),
            Map.entry("pptx", "slide"), Map.entry("ppt", "slide"), Map.entry("odp", "slide"));

    public boolean enabled()   { return enabled; }
    public String  serverUrl() { return serverUrl; }

    /** ¿El documento es un Office editable (por nombre o por la s3Key de su versión)? */
    public boolean esEditable(String nombreOExt) {
        return DOC_TYPE.containsKey(ext(nombreOExt));
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private boolean conJwt() {
        return jwtSecret != null && !jwtSecret.isBlank();
    }

    // ── Config del editor ────────────────────────────────────────────────────

    public Map<String, Object> construirConfig(String documentoId, String usuarioId, String modo) {
        boolean editar = !"view".equalsIgnoreCase(modo);
        if (!enabled) throw new IllegalStateException("OnlyOffice no está habilitado en el servidor.");
        DocumentoArchivo doc = docRepo.findById(documentoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado: " + documentoId));
        VersionDocumento v = versionRepo.findById(doc.getVersionActualId())
                .orElseThrow(() -> new IllegalStateException("Documento sin versión actual: " + documentoId));

        String fileType = primeraExtValida(ext(v.getS3Key()), ext(doc.getNombreLogico()));
        if (!DOC_TYPE.containsKey(fileType)) {
            throw new IllegalArgumentException(
                    "Este documento (" + fileType + ") no es un Office co-editable (docx/xlsx/pptx).");
        }
        String documentType = DOC_TYPE.get(fileType);
        // El DS descarga el original del BACKEND (misma red docker), no de S3:
        // la VPC no rutea la URL prefirmada global de S3, pero el backend sí baja
        // de S3 por SDK (endpoint regional). Token firmado y de corta vida.
        String dlToken = Jwts.builder().subject(documentoId)
                .expiration(java.util.Date.from(java.time.Instant.now().plus(Duration.ofMinutes(60))))
                .signWith(key()).compact();
        String urlOriginal = callbackBase + "/api/documentos/" + documentoId
                + "/onlyoffice/contenido?token=" + dlToken;
        String callbackUrl = callbackBase + "/api/documentos/" + documentoId + "/onlyoffice/callback";
        String nombreUsuario = usuarioRepo.findById(usuarioId)
                .map(u -> (safe(u.getNombre()) + " " + safe(u.getApellido())).trim())
                .filter(s -> !s.isBlank())
                .orElse("Funcionario");

        Map<String, Object> document = new LinkedHashMap<>();
        document.put("fileType", fileType);
        // key cambia con CADA versión → al guardar (nueva versión) OnlyOffice recarga.
        document.put("key", doc.getVersionActualId());
        document.put("title", tituloConExt(doc.getNombreLogico(), fileType));
        document.put("url", urlOriginal);
        document.put("permissions", Map.of("edit", editar, "download", true));

        Map<String, Object> editorConfig = new LinkedHashMap<>();
        editorConfig.put("mode", editar ? "edit" : "view");
        editorConfig.put("lang", "es");
        editorConfig.put("callbackUrl", callbackUrl);
        editorConfig.put("user", Map.of("id", usuarioId, "name", nombreUsuario));
        editorConfig.put("customization", Map.of("autosave", true, "forcesave", true));

        Map<String, Object> config = new LinkedHashMap<>();
        config.put("document", document);
        config.put("documentType", documentType);
        config.put("editorConfig", editorConfig);
        config.put("width", "100%");
        config.put("height", "100%");

        if (conJwt()) {
            config.put("token", Jwts.builder().claims(config).signWith(key()).compact());
        }
        return config;
    }

    // ── Force-save (guardar bajo demanda, p. ej. al pulsar "Guardar y volver") ─

    /**
     * Pide al Document Server que guarde YA la co-edición en curso (CommandService
     * "forcesave"). El DS responde al backend por el callback (status 6), que crea
     * una nueva versión en S3. error 0 = guardado disparado; error 4 = sin cambios
     * desde el último autosave (el documento ya está al día). Ambos son OK.
     */
    public Map<String, Object> forzarGuardado(String documentoId) {
        if (!enabled) throw new IllegalStateException("OnlyOffice no está habilitado en el servidor.");
        DocumentoArchivo doc = docRepo.findById(documentoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado: " + documentoId));
        String key = doc.getVersionActualId();

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("c", "forcesave");
        claims.put("key", key);
        String tok = conJwt() ? Jwts.builder().claims(claims).signWith(key()).compact() : null;
        String body = "{\"c\":\"forcesave\",\"key\":\"" + key + "\""
                + (tok != null ? ",\"token\":\"" + tok + "\"" : "") + "}";

        try {
            HttpClient cli = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
            HttpRequest req = HttpRequest.newBuilder(
                            URI.create(internalUrl.replaceAll("/$", "") + "/coauthoring/CommandService.ashx"))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> resp = cli.send(req, HttpResponse.BodyHandlers.ofString());
            log.info("OnlyOffice forcesave doc {} → HTTP {} {}", documentoId, resp.statusCode(), resp.body());
            return Map.of("status", resp.statusCode(), "respuesta", resp.body());
        } catch (Exception e) {
            log.error("OnlyOffice forcesave fallo {} → {}", documentoId, e.toString());
            throw new IllegalStateException("No se pudo forzar el guardado: " + e.getMessage());
        }
    }

    // ── Callback (guardado) ──────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public Map<String, Object> procesarCallback(String documentoId, Map<String, Object> body) {
        verificarToken(body);
        int status = body.get("status") instanceof Number n ? n.intValue() : 0;
        // 2 = listo para guardar (todos cerraron); 6 = force-save mientras editan.
        if (status == 2 || status == 6) {
            String url = str(body.get("url"));
            if (url == null) return Map.of("error", 0);
            String autorId = autorDe(body);
            try {
                byte[] bytes = descargar(url);
                DocumentoArchivo doc = docRepo.findById(documentoId).orElse(null);
                String ext = doc != null
                        ? versionRepo.findById(doc.getVersionActualId())
                              .map(v -> ext(v.getS3Key())).orElse("docx")
                        : "docx";
                versionadoService.crearNuevaVersionDesdeBytes(
                        documentoId, bytes, mimeDeExt(ext), ext, "Co-edición OnlyOffice", autorId);
                log.info("OnlyOffice: nueva versión guardada para documento {}", documentoId);
            } catch (Exception e) {
                log.error("OnlyOffice callback: fallo al guardar {} → {}", documentoId, e.toString());
                return Map.of("error", 1);
            }
        }
        return Map.of("error", 0);
    }

    /** Bytes del documento para que el DS lo descargue desde el backend (no de S3). */
    public DocContenido contenido(String documentoId, String token) {
        io.jsonwebtoken.Claims c;
        try {
            c = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            throw new SecurityException("Token de descarga inválido o expirado.");
        }
        if (!documentoId.equals(c.getSubject())) {
            throw new SecurityException("El token no corresponde a este documento.");
        }
        DocumentoArchivo doc = docRepo.findById(documentoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado"));
        VersionDocumento v = versionRepo.findById(doc.getVersionActualId())
                .orElseThrow(() -> new IllegalStateException("Documento sin versión actual"));
        return new DocContenido(s3.download(v.getS3Key()), mimeDeExt(ext(v.getS3Key())));
    }

    public record DocContenido(byte[] bytes, String mime) {}

    private void verificarToken(Map<String, Object> body) {
        if (!conJwt()) return;
        Object t = body.get("token");
        if (t == null) throw new SecurityException("Callback OnlyOffice sin token (JWT activo).");
        Jwts.parser().verifyWith(key()).build().parseSignedClaims(t.toString()); // lanza si inválido
    }

    @SuppressWarnings("unchecked")
    private String autorDe(Map<String, Object> body) {
        Object users = body.get("users");
        if (users instanceof List<?> l && !l.isEmpty() && l.get(0) != null) return l.get(0).toString();
        Object actions = body.get("actions");
        if (actions instanceof List<?> l && !l.isEmpty() && l.get(0) instanceof Map<?, ?> m) {
            Object uid = m.get("userid");
            if (uid != null) return uid.toString();
        }
        return "onlyoffice";
    }

    private byte[] descargar(String url) throws Exception {
        HttpClient cli = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET()
                .timeout(Duration.ofSeconds(60)).build();
        HttpResponse<byte[]> resp = cli.send(req, HttpResponse.BodyHandlers.ofByteArray());
        if (resp.statusCode() / 100 != 2) {
            throw new IllegalStateException("Descarga del editado HTTP " + resp.statusCode());
        }
        return resp.body();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static String ext(String nombre) {
        if (nombre == null) return "";
        int i = nombre.lastIndexOf('.');
        return i >= 0 ? nombre.substring(i + 1).toLowerCase() : "";
    }

    private static String primeraExtValida(String a, String b) {
        return DOC_TYPE.containsKey(a) ? a : b;
    }

    private static String tituloConExt(String nombre, String ext) {
        String base = (nombre == null || nombre.isBlank()) ? "documento" : nombre;
        return base.toLowerCase().endsWith("." + ext) ? base : base + "." + ext;
    }

    private static String mimeDeExt(String ext) {
        return switch (ext == null ? "" : ext.toLowerCase()) {
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "doc"  -> "application/msword";
            case "xls"  -> "application/vnd.ms-excel";
            case "ppt"  -> "application/vnd.ms-powerpoint";
            default     -> "application/octet-stream";
        };
    }

    private static String safe(String s)  { return s == null ? "" : s; }
    private static String str(Object o)   { return o == null ? null : o.toString(); }
}
