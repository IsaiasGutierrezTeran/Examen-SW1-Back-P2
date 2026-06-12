package com.example.demo.p8reportes;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TramiteRiesgoResponse {

    private String tramiteId;
    private Float probSuperarSla;
    private String nivel;
    private List<String> razones;
}
