package org.stringtecnologia.string_api.model.dto.fornecedor;

import jakarta.validation.constraints.NotNull;

public record FornecedorStatusDTO(@NotNull Boolean ativo) { }
