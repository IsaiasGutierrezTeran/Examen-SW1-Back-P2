package com.example.demo.services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Capa de almacenamiento de documentos sobre <b>Google Cloud Storage (GCS)</b>.
 *
 * <p>Mantiene el nombre y la API de la versión anterior (que usaba S3) para no
 * tocar el resto del código: {@code upload}, {@code presignedGet}, {@code exists},
 * {@code delete}, {@code bucket}. Internamente usa el SDK nativo de GCS.
 *
 * <p>Si {@code gcs.enabled=false}, el bean {@link Storage} no existe y este
 * servicio lanza {@link IllegalStateException} en cada operación — útil para
 * arrancar el backend en local sin credenciales de Google Cloud.
 *
 * <p>Autenticación: Application Default Credentials. En la VM de Compute Engine
 * usa la cuenta de servicio adjunta (sin claves). Las URLs firmadas (preview) se
 * firman vía IAM signBlob con esa misma cuenta.
 */
@Service
@Slf4j
public class S3StorageService {

    @Autowired
    private ObjectProvider<Storage> storageProvider;

    @Value("${gcs.bucket:}")
    private String bucket;

    @Value("${gcs.presigned-ttl-seconds:300}")
    private long presignedTtlSeconds;

    @Value("${gcs.enabled:false}")
    private boolean gcsEnabled;

    @PostConstruct
    void verificar() {
        if (!gcsEnabled) {
            log.warn("[GCS] gcs.enabled=false → las operaciones de almacenamiento lanzarán IllegalStateException");
            return;
        }
        if (bucket == null || bucket.isBlank()) {
            log.warn("[GCS] gcs.bucket vacío; revisa application.yml o GCS_BUCKET");
        } else {
            log.info("[GCS] habilitado · bucket={} · ttl-firmada={}s", bucket, presignedTtlSeconds);
        }
    }

    /**
     * Sube un binario a GCS.
     *
     * @param key         ruta completa dentro del bucket (p.ej. {@code tramites/t1/uuid-v1.pdf})
     * @param input       stream del archivo
     * @param contentType MIME type (puede ser null → application/octet-stream)
     * @param size        tamaño en bytes (no usado por GCS, se conserva por compatibilidad)
     */
    public void upload(String key, InputStream input, String contentType, long size) {
        Storage gcs = clienteRequerido();
        BlobInfo info = BlobInfo.newBuilder(BlobId.of(bucket, key))
                .setContentType(contentType != null ? contentType : "application/octet-stream")
                .build();
        try {
            gcs.createFrom(info, input);
        } catch (IOException e) {
            throw new IllegalStateException("Error subiendo a GCS: " + e.getMessage(), e);
        }
    }

    /** URL firmada GET con TTL configurable (V4). */
    public URL presignedGet(String key) {
        return presignedGet(key, Duration.ofSeconds(presignedTtlSeconds));
    }

    public URL presignedGet(String key, Duration ttl) {
        Storage gcs = clienteRequerido();
        BlobInfo info = BlobInfo.newBuilder(BlobId.of(bucket, key)).build();
        return gcs.signUrl(info, ttl.getSeconds(), TimeUnit.SECONDS,
                Storage.SignUrlOption.withV4Signature());
    }

    /** Instante en que expira una URL firmada generada ahora con el TTL por defecto. */
    public Instant calcularExpiracion() {
        return Instant.now().plusSeconds(presignedTtlSeconds);
    }

    public boolean exists(String key) {
        Storage gcs = clienteRequerido();
        Blob blob = gcs.get(BlobId.of(bucket, key));
        return blob != null && blob.exists();
    }

    public void delete(String key) {
        Storage gcs = clienteRequerido();
        gcs.delete(BlobId.of(bucket, key));
    }

    public String bucket() {
        return bucket;
    }

    public boolean enabled() {
        return gcsEnabled;
    }

    private Storage clienteRequerido() {
        Storage gcs = storageProvider.getIfAvailable();
        if (gcs == null) {
            throw new IllegalStateException(
                    "GCS deshabilitado o sin credenciales. Pon gcs.enabled=true y configura gcs.bucket.");
        }
        return gcs;
    }
}
