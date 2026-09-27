package org.stringtecnologia.string_api.model.dto.security;

import java.util.Set;

public record UsuarioAutenticadoDTO(

        String subject,

        String username,

        String nome,

        String email,

        Set<String> roles,

        Set<String> permissoes

) {
}
