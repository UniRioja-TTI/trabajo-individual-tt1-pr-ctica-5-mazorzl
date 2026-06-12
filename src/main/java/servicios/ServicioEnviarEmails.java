package servicios;

import interfaces.InterfazEnviarEmails;
import modelo.Destinatario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServicioEnviarEmails implements InterfazEnviarEmails {
    private final Logger logger = LoggerFactory.getLogger(ServicioEnviarEmails.class);
    @Override
    public boolean enviarEmail(Destinatario dest, String email) {
        logger.info("Enviando email a: " + dest + " Contenido: " + email);
        return true;
    }
}
