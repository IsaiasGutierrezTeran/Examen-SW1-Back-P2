package com.example.demo.p3politicas;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "colaboraciones_diagrama")
public class ColaboracionDiagrama {

    @Id
    private String id;

    private String diagramaId;
    private String adminInvitadorId;
    private String invitadoId;
    private String rolColaboracion;
    private String estado;

    private LocalDateTime fechaInvitacion;
    private LocalDateTime fechaRespuesta;
}

