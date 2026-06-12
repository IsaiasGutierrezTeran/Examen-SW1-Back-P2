package com.example.demo.p4tramites;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreviewDocumentoResponse {
    private String urlPreview;
    private String mimeType;
    private Instant expiraEn;
}
