package org.stringtecnologia.string_api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.stringtecnologia.string_api.services.NomeSugestaoCacheService;


@Component
@RequiredArgsConstructor
@Slf4j
@Order(100)
public class NomeSugestaoCacheBootstrap
        implements ApplicationRunner {

    private final NomeSugestaoCacheService
            cacheService;

    @Override
    public void run(
            ApplicationArguments args
    ) {

        log.info(
                "Inicializando cache Redis "
                        + "de sugestões de nomes..."
        );

        cacheService.reconstruir();
    }
}