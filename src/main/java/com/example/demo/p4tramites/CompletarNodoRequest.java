package com.example.demo.p4tramites;

import lombok.Data;

@Data
public class CompletarNodoRequest {

    private String funcionarioId;

    private String decision;

    private String notas;

    private String nodoId;
}
