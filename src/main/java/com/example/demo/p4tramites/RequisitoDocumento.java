package com.example.demo.p4tramites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequisitoDocumento {

    public static final String CLIENTE = "CLIENTE";
    public static final String FUNCIONARIO = "FUNCIONARIO";

    private String documentoId;

    private String proveedor;

    private boolean obligatorio;
}
