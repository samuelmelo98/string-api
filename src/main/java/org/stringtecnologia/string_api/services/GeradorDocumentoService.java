package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.documento.dto.AdiantamentoDocumentoDTO;
import org.stringtecnologia.string_api.documento.dto.DocumentoInternoDTO;
import org.stringtecnologia.string_api.documento.dto.DocumentoResponseDTO;
import org.stringtecnologia.string_api.documento.dto.OrdemServicoDocumentoDTO;
import org.stringtecnologia.string_api.util.HtmlNormalizerUtil;
import org.stringtecnologia.string_api.util.TipoDocumentoDominio;


import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class GeradorDocumentoService {

    private final DocumentoTemplateService documentoTemplateService;

    private final PdfGeneratorService pdfGeneratorService;

    private final PdfGeneratorPlaywrightService pdfGeneratorPlaywrightService;

    private final DocumentoService documentoService;

    public DocumentoResponseDTO gerarTermoAdiantamento(
            Long adiantamentoId
    ) {

        // =====================================================
        // DTO TEMPLATE
        // =====================================================

        AdiantamentoDocumentoDTO dto =
                new AdiantamentoDocumentoDTO(
                        adiantamentoId,
                        "Samuel Silva",
                        BigDecimal.valueOf(1500),
                        LocalDate.now()
                );

        // =====================================================
        // TEMPLATE -> HTML
        // =====================================================

        String html =
                documentoTemplateService.gerarAdiantamento(
                        "termo-adiantamento",
                        dto
                );

        // =====================================================
        // HTML -> PDF
        // =====================================================



        byte[] pdf =
                pdfGeneratorPlaywrightService.generate(HtmlNormalizerUtil.normalize(html));

        // =====================================================
        // DTO ARQUIVO
        // =====================================================

        DocumentoInternoDTO arquivo =
                new DocumentoInternoDTO(
                        "termo-adiantamento.pdf",
                        "application/pdf",
                        pdf
                );

        // =====================================================
        // SALVA
        // =====================================================

        return documentoService.salvarInterno(
                adiantamentoId,
                arquivo,
                TipoDocumentoDominio
                        .TERMO_ADIANTAMENTO_PARA_ASSINAR
        );
    }


    public DocumentoResponseDTO gerarOrdemServico(String slug,
            Long equipamentoId
    ) {

        OrdemServicoDocumentoDTO dto =
                new OrdemServicoDocumentoDTO(
                        equipamentoId,
                        "OS-2026-0001",
                        "Samuel Silva",
                        "Notebook Dell Latitude 5420",
                        "Troca de SSD e instalação do sistema operacional",
                        BigDecimal.valueOf(350.00),
                        LocalDate.now(),
                        LocalDate.now()
                );

        String html =
                documentoTemplateService.gerar(
                        slug,
                        dto
                );

        byte[] pdf =
                pdfGeneratorPlaywrightService.generate(
                        HtmlNormalizerUtil.normalize(html)
                );

        DocumentoInternoDTO arquivo =
                new DocumentoInternoDTO(
                        "ordem-servico.pdf",
                        "application/pdf",
                        pdf
                );

        return documentoService.salvarInterno(
                equipamentoId,
                arquivo,
                TipoDocumentoDominio.ORDEM_SERVICO
        );
    }

}