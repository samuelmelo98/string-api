
package org.stringtecnologia.string_api.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.stringtecnologia.string_api.model.dto.fornecedor.FornecedorOpcaoDTO;
import org.stringtecnologia.string_api.model.dto.fornecedor.FornecedorRequestDTO;
import org.stringtecnologia.string_api.model.dto.fornecedor.FornecedorResponseDTO;
import org.stringtecnologia.string_api.model.entities.Fornecedor;
import org.stringtecnologia.string_api.model.enums.TipoPessoa;
import org.stringtecnologia.string_api.repository.FornecedorRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

@Service
public class FornecedorService {

    private final FornecedorRepository repository;

    public FornecedorService(FornecedorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FornecedorResponseDTO criar(FornecedorRequestDTO dto) {
        String documento = validarDocumento(
                dto.tipoPessoa(), dto.cpfCnpj()
        );

        verificarDuplicidade(documento, null);

        Fornecedor fornecedor = new Fornecedor();
        preencher(fornecedor, dto, documento);

        return salvar(fornecedor);
    }

    @Transactional
    public FornecedorResponseDTO atualizar(
            Long id,
            FornecedorRequestDTO dto
    ) {
        Fornecedor fornecedor = buscarEntidade(id);

        String documento = validarDocumento(
                dto.tipoPessoa(), dto.cpfCnpj()
        );

        verificarDuplicidade(documento, id);
        preencher(fornecedor, dto, documento);

        return salvar(fornecedor);
    }

    @Transactional(readOnly = true)
    public FornecedorResponseDTO buscar(Long id) {
        return FornecedorResponseDTO.from(
                buscarEntidade(id)
        );
    }

    @Transactional(readOnly = true)
    public Page<FornecedorResponseDTO> listar(
            String busca,
            Boolean ativo,
            Pageable pageable
    ) {
        return pesquisar(busca, ativo, pageable)
                .map(FornecedorResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public List<FornecedorOpcaoDTO> opcoes(String busca) {

        Pageable primeiros = PageRequest.of(
                0,
                20,
                Sort.by("razaoSocial").ascending()
        );

        return pesquisar(busca, true, primeiros)
                .map(f -> new FornecedorOpcaoDTO(
                        f.getFornecedorId(),
                        f.getRazaoSocial(),
                        f.getCpfCnpj()
                ))
                .getContent();
    }

    /**
     * Centraliza os filtros e evita parâmetros
     * String nulos na consulta JPQL.
     */
    private Page<Fornecedor> pesquisar(
            String busca,
            Boolean ativo,
            Pageable pageable
    ) {
        boolean ignorarBusca =
                busca == null || busca.isBlank();

        String termo = ignorarBusca
                ? "%"
                : "%" + busca.trim()
                .toLowerCase(Locale.ROOT) + "%";

        return repository.buscarFiltrados(
                ativo,
                ignorarBusca,
                termo,
                pageable
        );
    }

    @Transactional
    public FornecedorResponseDTO alterarStatus(
            Long id,
            boolean ativo
    ) {
        Fornecedor fornecedor = buscarEntidade(id);
        fornecedor.setAtivo(ativo);

        return FornecedorResponseDTO.from(
                repository.saveAndFlush(fornecedor)
        );
    }

    private Fornecedor buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Fornecedor não encontrado"
                        )
                );
    }

    private void verificarDuplicidade(
            String documento,
            Long id
    ) {
        boolean existe = id == null
                ? repository.existsByCpfCnpj(documento)
                : repository.existsByCpfCnpjAndFornecedorIdNot(
                documento, id
        );

        if (existe) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "CPF/CNPJ já cadastrado"
            );
        }
    }

    private FornecedorResponseDTO salvar(
            Fornecedor fornecedor
    ) {
        try {
            return FornecedorResponseDTO.from(
                    repository.saveAndFlush(fornecedor)
            );
        } catch (DataIntegrityViolationException e) {
            if (violacaoUnicidade(e)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "CPF/CNPJ já cadastrado",
                        e
                );
            }

            throw e;
        }
    }

    private boolean violacaoUnicidade(Throwable erro) {
        Throwable causa = erro;

        while (causa != null) {
            if (causa instanceof SQLException sql
                    && "23505".equals(sql.getSQLState())) {
                return true;
            }

            causa = causa.getCause();
        }

        return false;
    }

    private void preencher(
            Fornecedor f,
            FornecedorRequestDTO d,
            String documento
    ) {
        f.setTipoPessoa(d.tipoPessoa());
        f.setRazaoSocial(d.razaoSocial().trim());
        f.setNomeFantasia(limpar(d.nomeFantasia()));
        f.setCpfCnpj(documento);
        f.setInscricaoEstadual(limpar(d.inscricaoEstadual()));
        f.setEmail(limpar(d.email()));
        f.setTelefone(limpar(d.telefone()));
        f.setWhatsapp(limpar(d.whatsapp()));
        f.setCep(validarCep(d.cep()));
        f.setEndereco(limpar(d.endereco()));
        f.setNumero(limpar(d.numero()));
        f.setComplemento(limpar(d.complemento()));
        f.setBairro(limpar(d.bairro()));
        f.setCidade(limpar(d.cidade()));
        f.setEstado(validarEstado(d.estado()));
        f.setObservacao(limpar(d.observacao()));
    }

    private String limpar(String texto) {
        return texto == null || texto.isBlank()
                ? null
                : texto.trim();
    }

    private String validarCep(String cep) {
        if (cep == null || cep.isBlank()) {
            return null;
        }

        String digitos = cep.replaceAll("\\D", "");

        if (digitos.length() != 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "CEP inválido"
            );
        }

        return digitos;
    }

    private String validarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }

        String uf = estado.trim()
                .toUpperCase(Locale.ROOT);

        if (!uf.matches("[A-Z]{2}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "UF inválida"
            );
        }

        return uf;
    }

    private String validarDocumento(
            TipoPessoa tipo,
            String valor
    ) {
        if (tipo == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de pessoa obrigatório"
            );
        }

        String documento = valor == null
                ? ""
                : valor.replaceAll("\\D", "");

        boolean valido = tipo == TipoPessoa.PF
                ? validarCpf(documento)
                : validarCnpj(documento);

        if (!valido) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "CPF/CNPJ inválido para o tipo de pessoa"
            );
        }

        return documento;
    }

    private boolean validarCpf(String cpf) {
        if (!cpf.matches("\\d{11}")
                || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += Character.digit(cpf.charAt(i), 10)
                    * (10 - i);
        }

        int dig1 = (soma * 10) % 11;
        if (dig1 == 10) dig1 = 0;

        if (dig1 != Character.digit(cpf.charAt(9), 10)) {
            return false;
        }

        soma = 0;

        for (int i = 0; i < 10; i++) {
            soma += Character.digit(cpf.charAt(i), 10)
                    * (11 - i);
        }

        int dig2 = (soma * 10) % 11;
        if (dig2 == 10) dig2 = 0;

        return dig2 == Character.digit(
                cpf.charAt(10), 10
        );
    }

    private boolean validarCnpj(String cnpj) {
        if (!cnpj.matches("\\d{14}")
                || cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        int[] pesos1 = {
                5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2
        };

        int[] pesos2 = {
                6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2
        };

        return digitoCnpj(cnpj, pesos1)
                == Character.digit(cnpj.charAt(12), 10)
                && digitoCnpj(cnpj, pesos2)
                == Character.digit(cnpj.charAt(13), 10);
    }

    private int digitoCnpj(
            String cnpj,
            int[] pesos
    ) {
        int soma = 0;

        for (int i = 0; i < pesos.length; i++) {
            soma += Character.digit(cnpj.charAt(i), 10)
                    * pesos[i];
        }

        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
