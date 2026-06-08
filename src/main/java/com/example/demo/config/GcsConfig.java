package com.example.demo.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean de Google Cloud Storage (GCS) — almacenamiento de documentos en GCP.
 *
 * <p>Se crea solo cuando {@code gcs.enabled=true}, así el backend puede arrancar
 * sin GCS en local. La autenticación usa <b>Application Default Credentials</b>:
 * en la VM de Compute Engine toma automáticamente la cuenta de servicio adjunta
 * (vía el metadata server), sin necesidad de claves — lo que además respeta la
 * política de la organización que prohíbe crear claves de service account.
 *
 * <p>Para firmar URLs (preview), esa cuenta de servicio necesita el permiso
 * {@code iam.serviceAccounts.signBlob} (rol {@code roles/iam.serviceAccountTokenCreator}
 * sobre sí misma), que ya está concedido.
 */
@Configuration
@ConditionalOnProperty(name = "gcs.enabled", havingValue = "true")
public class GcsConfig {

    /** Opcional: si se define, fija el proyecto; si no, lo toma de las credenciales. */
    @Value("${gcs.project-id:}")
    private String projectId;

    @Bean
    public Storage gcsStorage() {
        StorageOptions.Builder builder = StorageOptions.newBuilder();
        if (projectId != null && !projectId.isBlank()) {
            builder.setProjectId(projectId);
        }
        return builder.build().getService();
    }
}
