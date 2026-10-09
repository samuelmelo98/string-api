package org.stringtecnologia.string_api.model.dto.fornecedor;

import org.stringtecnologia.string_api.model.entities.Fornecedor;
import org.stringtecnologia.string_api.model.enums.TipoPessoa;

import java.time.Instant;

public record FornecedorResponseDTO(
        Long fornecedorId, TipoPessoa tipoPessoa, String razaoSocial,
        String nomeFantasia, String cpfCnpj, String inscricaoEstadual,
        String email, String telefone, String whatsapp,
        String cep, String endereco, String numero, String complemento,
        String bairro, String cidade, String estado, String observacao,
        boolean ativo, Instant dataCadastro, Instant dataAtualizacao
) {
    public static FornecedorResponseDTO from(Fornecedor f) {
        return new FornecedorResponseDTO(
                f.getFornecedorId(), f.getTipoPessoa(), f.getRazaoSocial(),
                f.getNomeFantasia(), f.getCpfCnpj(), f.getInscricaoEstadual(),
                f.getEmail(), f.getTelefone(), f.getWhatsapp(),
                f.getCep(), f.getEndereco(), f.getNumero(), f.getComplemento(),
                f.getBairro(), f.getCidade(), f.getEstado(), f.getObservacao(),
                f.isAtivo(), f.getDataCadastro(), f.getDataAtualizacao()
        );
    }
}
