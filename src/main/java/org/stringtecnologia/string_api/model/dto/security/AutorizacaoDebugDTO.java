package org.stringtecnologia.string_api.model.dto.security;


import java.util.Set;

public record AutorizacaoDebugDTO(

        String subject,

        String username,

        String clientId,

        Set<String> roles,

        Set<String> permissoes

) {
}
