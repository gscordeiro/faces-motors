package dev.gscordeiro.facesmotors.web;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class H2ConsoleStarter {

    private static final Logger logger = LoggerFactory.getLogger(H2ConsoleStarter.class);

    private Server webServer;

    void onStart(@Observes StartupEvent event) throws Exception {
        webServer = Server.createWebServer("-webPort", "8082", "-webAllowOthers");
        webServer.start();
        logger.info("H2 Console disponível em: http://localhost:8082");
        logger.info("JDBC URL para conexão: jdbc:h2:mem:facesmotors");
        logger.info("Usuário: sa  |  Senha: (vazia)");
    }

    void onStop(@Observes ShutdownEvent event) {
        if (webServer != null) {
            webServer.stop();
        }
    }
}
