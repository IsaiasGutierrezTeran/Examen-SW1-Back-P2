package com.example.demo.p5bandeja;

import java.util.List;
import lombok.Data;

@Data
public class GuardarSeccionRequest {
    private List<CampoValorDto> campos;
    private String notasOperativas;
}
