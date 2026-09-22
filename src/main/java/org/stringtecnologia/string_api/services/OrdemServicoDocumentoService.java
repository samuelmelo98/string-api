package org.stringtecnologia.string_api.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.model.entities.Aparelho;
import org.stringtecnologia.string_api.model.entities.Cliente;
import org.stringtecnologia.string_api.model.entities.DocumentProcessor;
import org.stringtecnologia.string_api.model.entities.DocumentoTemplate;
import org.stringtecnologia.string_api.model.entities.OrdemServico;
import org.stringtecnologia.string_api.repository.DocumentoTemplateRepository;
import org.stringtecnologia.string_api.repository.OrdemServicoRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrdemServicoDocumentoService {

    private static final String SLUG = "ordem-servico";

    private static final DateTimeFormatter DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final OrdemServicoRepository ordemServicoRepository;

    private final DocumentoTemplateRepository documentoTemplateRepository;

    private final DocumentProcessor documentProcessor;

    @Value("${string.empresa.cnpj}")
    private String empresaCnpj;

    @Value("${string.empresa.telefone}")
    private String empresaTelefone;

    @Value("${string.empresa.whatsapp}")
    private String empresaWhatsapp;

    @Value("${string.ordem-servico.consulta-base-url}")
    private String consultaBaseUrl;

    public OrdemServicoDocumentoService(
            OrdemServicoRepository ordemServicoRepository,
            DocumentoTemplateRepository documentoTemplateRepository,
            @Qualifier("mustacheDocumentProcessor")
            DocumentProcessor documentProcessor
    ) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.documentoTemplateRepository = documentoTemplateRepository;
        this.documentProcessor = documentProcessor;
    }

    @Transactional(readOnly = true)
    public String gerar(Long ordemServicoId) {

        OrdemServico ordem = ordemServicoRepository
                .findById(ordemServicoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Ordem de serviço não encontrada: "
                                        + ordemServicoId
                        )
                );

        DocumentoTemplate template = documentoTemplateRepository
                .findFirstBySlugAndActiveTrueOrderByVersionDesc(
                        SLUG
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Template ativo '"
                                        + SLUG
                                        + "' não encontrado."
                        )
                );

        Map<String, Object> variaveis =
                criarVariaveis(ordem);

        return documentProcessor.process(
                template.getTemplate(),
                variaveis
        );
    }

    private Map<String, Object> criarVariaveis(
            OrdemServico ordem
    ) {

        Aparelho aparelho =
                ordem.getAparelho();

        Cliente cliente =
                ordem.getCliente();

        Map<String, Object> root =
                new HashMap<>();

        /*
         * ORDEM DE SERVIÇO
         */
        Map<String, Object> dadosOrdem =
                new HashMap<>();

        dadosOrdem.put(
                "id",
                ordem.getOrdemServicoId()
        );

        dadosOrdem.put(
                "numero",
                valor(ordem.getNumero())
        );

        dadosOrdem.put(
                "status",
                ordem.getStatus() != null
                        ? valor(
                        ordem.getStatus()
                                .getDescricao()
                )
                        : ""
        );

        dadosOrdem.put(
                "statusCodigo",
                ordem.getStatus() != null
                        ? valor(
                        ordem.getStatus()
                                .getCodigo()
                )
                        : ""
        );

        dadosOrdem.put(
                "dataAbertura",
                ordem.getDataAbertura() != null
                        ? ordem.getDataAbertura()
                        .format(DATA_HORA)
                        : ""
        );

        dadosOrdem.put(
                "dataAtualizacao",
                ordem.getDataAtualizacao() != null
                        ? ordem.getDataAtualizacao()
                        .format(DATA_HORA)
                        : ""
        );

        dadosOrdem.put(
                "dataAprovacao",
                ordem.getDataAprovacao() != null
                        ? ordem.getDataAprovacao()
                        .format(DATA_HORA)
                        : ""
        );

        dadosOrdem.put(
                "dataInicioServico",
                ordem.getDataInicioServico() != null
                        ? ordem.getDataInicioServico()
                        .format(DATA_HORA)
                        : ""
        );

        dadosOrdem.put(
                "dataConclusao",
                ordem.getDataConclusao() != null
                        ? ordem.getDataConclusao()
                        .format(DATA_HORA)
                        : ""
        );

        dadosOrdem.put(
                "dataEntrega",
                ordem.getDataEntrega() != null
                        ? ordem.getDataEntrega()
                        .format(DATA_HORA)
                        : ""
        );

        dadosOrdem.put(
                "diagnostico",
                valor(ordem.getDiagnostico())
        );

        dadosOrdem.put(
                "solucao",
                valor(ordem.getSolucao())
        );

        dadosOrdem.put(
                "observacao",
                valor(ordem.getObservacao())
        );

        dadosOrdem.put(
                "valorOrcamento",
                formatarMoeda(ordem.getValorOrcamento())
        );

        dadosOrdem.put(
                "valorFinal",
                formatarMoeda(ordem.getValorFinal())
        );

        /*
         * CLIENTE
         */
        Map<String, Object> dadosCliente =
                new HashMap<>();

        dadosCliente.put(
                "id",
                cliente.getClienteId()
        );

        dadosCliente.put(
                "nome",
                valor(cliente.getNome())
        );

        dadosCliente.put(
                "cpf",
                formatarCpf(cliente.getCpf())
        );

        dadosCliente.put(
                "telefone",
                formatarTelefone(cliente.getTelefone())
        );

        dadosCliente.put(
                "email",
                valor(cliente.getEmail())
        );

        dadosCliente.put(
                "endereco",
                valor(cliente.getEndereco())
        );

        dadosCliente.put(
                "cidade",
                valor(cliente.getCidade())
        );

        dadosCliente.put(
                "estado",
                valor(cliente.getEstado())
        );

        dadosCliente.put(
                "cep",
                valor(cliente.getCep())
        );

        /*
         * APARELHO
         */
        Map<String, Object> dadosAparelho =
                new HashMap<>();

        dadosAparelho.put(
                "id",
                aparelho.getAparelhoId()
        );

        dadosAparelho.put(
                "tipo",
                aparelho.getTipo() != null
                        ? valor(
                        aparelho.getTipo()
                                .getNome()
                )
                        : ""
        );

        dadosAparelho.put(
                "marca",
                aparelho.getMarca() != null
                        ? valor(
                        aparelho.getMarca()
                                .getNome()
                )
                        : ""
        );

        dadosAparelho.put(
                "modelo",
                valor(aparelho.getModelo())
        );

        dadosAparelho.put(
                "modeloComercial",
                valor(
                        aparelho.getModeloComercial()
                )
        );

        dadosAparelho.put(
                "numeroSerie",
                valor(
                        aparelho.getNumeroSerie()
                )
        );

        dadosAparelho.put(
                "descricao",
                valor(
                        aparelho.getDescricao()
                )
        );

        dadosAparelho.put(
                "defeito",
                valor(
                        ordem.getDefeitoRelatado()
                )
        );

        /*
         * ROOT MUSTACHE
         */
        root.put(
                "ordem",
                dadosOrdem
        );

        root.put(
                "cliente",
                dadosCliente
        );

        root.put(
                "aparelho",
                dadosAparelho
        );

        String urlConsulta =
                montarUrlConsulta(
                        ordem
                );

        Map<String, Object> empresa =
                new HashMap<>();

        empresa.put(
                "nome",
                "String Tecnologia"
        );

        empresa.put(
                "cnpj",
                empresaCnpj
        );

        empresa.put(
                "telefone",
                empresaTelefone
        );

        empresa.put(
                "whatsapp",
                empresaWhatsapp
        );

        empresa.put(
                "urlConsulta",
                urlConsulta
        );

        empresa.put(
                "qrCode",
                gerarQrCodeBase64(
                        urlConsulta
                )
        );

        root.put(
                "empresa",
                empresa
        );

        return root;
    }

    private String montarUrlConsulta(
            OrdemServico ordem
    ) {

        if (ordem.getConsultaToken() == null
                || ordem.getConsultaToken().isBlank()) {

            throw new IllegalStateException(
                    "A ordem de serviço não possui token de consulta."
            );
        }

        return consultaBaseUrl
                + "/"
                + ordem.getNumero()
                + "?t="
                + ordem.getConsultaToken();
    }


    private String valor(String valor) {
        return valor == null
                ? ""
                : valor;
    }

    private String formatarCpf(String cpf) {

        if (cpf == null) {
            return "";
        }

        String numeros =
                cpf.replaceAll("\\D", "");

        if (numeros.length() != 11) {
            return cpf;
        }

        return numeros.replaceFirst(
                "(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
                "$1.$2.$3-$4"
        );
    }

    private String formatarTelefone(String telefone) {

        if (telefone == null) {
            return "";
        }

        String numeros =
                telefone.replaceAll("\\D", "");

        if (numeros.length() == 11) {
            return numeros.replaceFirst(
                    "(\\d{2})(\\d{5})(\\d{4})",
                    "($1) $2-$3"
            );
        }

        if (numeros.length() == 10) {
            return numeros.replaceFirst(
                    "(\\d{2})(\\d{4})(\\d{4})",
                    "($1) $2-$3"
            );
        }

        return telefone;
    }

    private String formatarMoeda(
            java.math.BigDecimal valor
    ) {

        if (valor == null) {
            return "";
        }

        return java.text.NumberFormat
                .getCurrencyInstance(
                        new java.util.Locale(
                                "pt",
                                "BR"
                        )
                )
                .format(valor);
    }

    private String gerarQrCodeBase64(String conteudo) {

        try {
            QRCodeWriter qrCodeWriter =
                    new QRCodeWriter();

            BitMatrix matrix =
                    qrCodeWriter.encode(
                            conteudo,
                            BarcodeFormat.QR_CODE,
                            180,
                            180
                    );

            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(
                    matrix,
                    "PNG",
                    output
            );

            return "data:image/png;base64,"
                    + Base64.getEncoder()
                    .encodeToString(
                            output.toByteArray()
                    );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Erro ao gerar QR Code da ordem de servico.",
                    e
            );
        }
    }
}
