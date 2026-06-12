package com.example.demo.p4tramites;

import java.util.List;
import lombok.Data;

@Data
public class DevolverTramiteRequest {
    private String nodoDestinoId;
    private String observaciones;

    private List<String> documentosObservados;
}
