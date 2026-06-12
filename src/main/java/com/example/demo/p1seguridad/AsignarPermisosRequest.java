package com.example.demo.p1seguridad;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class AsignarPermisosRequest {

    @NotEmpty(message = "Debe especificar al menos un permiso")
    private List<String> permisos;
}
