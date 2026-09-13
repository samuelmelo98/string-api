package org.stringtecnologia.string_api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.env.Environment;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupLogger {

    private static final Logger log =
            LoggerFactory.getLogger(ApplicationStartupLogger.class);

    private final Environment environment;

    public ApplicationStartupLogger(Environment environment) {
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applicationReady() {

        String version = environment.getProperty("APP_VERSION", "dev");

        log.info("==========================================================");
        log.info(" STRING-API INICIADA");
        log.info(" Versao da imagem: {}", version);
        log.info("==========================================================");
    }
}