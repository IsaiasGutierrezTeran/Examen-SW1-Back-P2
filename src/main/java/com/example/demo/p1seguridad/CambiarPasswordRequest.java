package com.example.demo.p1seguridad;

import lombok.Data;

@Data
public class CambiarPasswordRequest {
    private String actual;
    private String nueva;
}
