package com.example.demo.p4tramites;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActividadDocumentosDTO {
    private String actividadId;
    private String actividadNombre;
    private List<DocumentoInfoDTO> documentosRequeridos;
}
