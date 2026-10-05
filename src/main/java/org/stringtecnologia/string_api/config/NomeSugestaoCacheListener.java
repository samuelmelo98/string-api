package org.stringtecnologia.string_api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.stringtecnologia.string_api.model.dto.nome.NomeSugestaoAlteradoEvent;
import org.stringtecnologia.string_api.services.NomeSugestaoCacheService;

@Component
@RequiredArgsConstructor
public class NomeSugestaoCacheListener {

    private final NomeSugestaoCacheService
            cacheService;

    @TransactionalEventListener(
            phase =
                    TransactionPhase.AFTER_COMMIT
    )
    public void onAlterado(
            NomeSugestaoAlteradoEvent event
    ) {

        cacheService
                .sincronizar(
                        event
                );
    }
}
