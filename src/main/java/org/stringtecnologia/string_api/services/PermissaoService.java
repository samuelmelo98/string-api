package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.repository.PerfilPermissaoRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermissaoService {

    private final PerfilPermissaoRepository perfilPermissaoRepository;

    @Transactional(readOnly = true)
    public Set<String> buscarPorPerfis(
            Collection<String> perfis
    ) {

        if (perfis == null || perfis.isEmpty()) {
            return Set.of();
        }

        Set<String> perfisNormalizados = new HashSet<>();

        for (String perfil : perfis) {

            if (perfil == null || perfil.isBlank()) {
                continue;
            }

            perfisNormalizados.add(
                    perfil.trim()
                            .toUpperCase(Locale.ROOT)
            );
        }

        if (perfisNormalizados.isEmpty()) {
            return Set.of();
        }

        return perfilPermissaoRepository
                .buscarCodigosPermissoesPorPerfis(
                        perfisNormalizados
                );
    }
}
