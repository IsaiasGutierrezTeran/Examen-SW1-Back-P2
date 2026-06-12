package com.example.demo.p8reportes;

import com.example.demo.p9ia.AgenteAsistenciaService;
import com.example.demo.p9ia.AgenteRequest;
import com.example.demo.p9ia.AgenteResponse;
import com.example.demo.p9ia.IaProxyService;
import com.example.demo.p9ia.TranscripcionVoz;
import com.example.demo.p9ia.TranscripcionVozRepository;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AiIntegrationService {

    @Autowired private TranscripcionVozRepository vozRepo;
    @Autowired private LogAgenteRepository agenteRepo;
    @Autowired private AgenteAsistenciaService agenteKb;
    @Autowired private IaProxyService iaProxy;

    public TranscripcionVoz transcribirAudio(String seccionId, MultipartFile archivo, String funcionarioId) {
        Map<String, Object> resp = iaProxy.vozAFormulario(archivo, "[]");
        Object textoObj = resp.get("texto_transcrito");
        String texto = textoObj != null ? textoObj.toString() : "";

        TranscripcionVoz tv = new TranscripcionVoz();
        tv.setSeccionId(seccionId);
        tv.setFuncionarioId(funcionarioId);
        tv.setTextoTranscrito(texto);
        tv.setDuracionSegundos(0.0f);
        tv.setConfianzaTranscripcion(0.9f);
        tv.setFechaTranscripcion(LocalDateTime.now());
        return vozRepo.save(tv);
    }

    public AgenteResponse consultarAgente(AgenteRequest input, String usuarioId, String rolId) {
        long start = System.currentTimeMillis();
        AgenteResponse resp = agenteKb.responderInteligente(input, rolId);
        long end = System.currentTimeMillis();

        LogAgente lg = new LogAgente();
        lg.setUsuarioId(usuarioId);
        lg.setContextoModulo(input.getModuloActivo());
        lg.setContextoRol(rolId);
        lg.setContextoTramiteId(input.getTramiteIdOpcional());
        lg.setConsultaUsuario(input.getConsulta());
        lg.setRespuestaAgente(resp.getRespuesta());
        lg.setTiempoRespuestaMs((float) (end - start));
        lg.setFueUtil(false);
        lg.setTimestamp(LocalDateTime.now());
        lg = agenteRepo.save(lg);

        resp.setIdLogBaseDatos(lg.getId());
        return resp;
    }
}
