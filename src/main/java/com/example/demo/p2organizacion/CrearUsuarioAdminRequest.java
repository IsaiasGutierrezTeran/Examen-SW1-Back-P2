package com.example.demo.p2organizacion;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class CrearUsuarioAdminRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    @Pattern(regexp = "funcionario|administrador",
             message = "Solo se puede crear funcionario o administrador por esta vía")
    private String tipo;

    private String telefono;
    private List<String> departamentosIds;
}
