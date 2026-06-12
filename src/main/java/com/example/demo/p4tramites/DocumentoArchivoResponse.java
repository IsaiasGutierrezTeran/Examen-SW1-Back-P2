package com.example.demo.p4tramites;

import java.time.Instant;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoArchivoResponse {

    private String documentoArchivoId;
    private String versionId;
    private int numeroVersion;
    private String nombreLogico;
    private String tipoDocumento;
    private long tamanoBytes;
    private String mimeType;
    private String autorId;
    private LocalDateTime fechaCreacion;

    private String urlPreview;
    private Instant expiraEn;
}
