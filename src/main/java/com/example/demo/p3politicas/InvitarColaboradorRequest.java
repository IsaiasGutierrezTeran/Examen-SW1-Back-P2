package com.example.demo.p3politicas;

import lombok.Data;

@Data
public class InvitarColaboradorRequest {
    private String usuarioInvitadoId;
    private String permisos;
}
