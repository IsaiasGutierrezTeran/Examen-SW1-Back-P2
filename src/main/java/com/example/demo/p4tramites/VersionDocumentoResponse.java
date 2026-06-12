package com.example.demo.p4tramites;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionDocumentoResponse {
    private String versionId;
    private int numeroVersion;
    private String autorId;
    private List<String> coautores;
    private String comentarioCambio;
    private long tamanoBytes;
    private String mimeType;
    private String hashSha256;
    private LocalDateTime fechaCreacion;
    private boolean esActual;
}
