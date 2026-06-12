package com.example.demo.p8reportes;

import java.util.List;
import lombok.Data;

@Data
public class LineaTiempoResponse {
    private String tramiteId;
    private String estadoActual;
    private List<HitoDTO> hitos;
}
