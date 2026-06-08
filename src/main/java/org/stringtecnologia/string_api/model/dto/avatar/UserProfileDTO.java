package org.stringtecnologia.string_api.model.dto.avatar;

public record UserProfileDTO(
        Long id,
        String username,
        String email,
        String name,
        String avatar
) {}