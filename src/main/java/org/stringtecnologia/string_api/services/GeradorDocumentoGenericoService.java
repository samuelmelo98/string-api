package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.documento.dto.DocumentoInternoDTO;
import org.stringtecnologia.string_api.documento.dto.DocumentoResponseDTO;
import org.stringtecnologia.string_api.util.HtmlNormalizerUtil;
import org.stringtecnologia.string_api.util.TipoDocumentoDominio;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeradorDocumentoGenericoService {



        private final DocumentoTemplateService documentoTemplateService;
        private final DocumentoTemplateGenericoService documentoTemplateGenericoService;
        private final PdfGeneratorPlaywrightService pdfGeneratorPlaywrightService;
        private final DocumentoService documentoService;

        public <T> DocumentoResponseDTO gerarDocumento(
                String slug,
                T dto,
                Long referenciaId,
                TipoDocumentoDominio tipoDocumento,
                String contexto
        ) {

            String html =
                    documentoTemplateGenericoService.gerar(
                            slug,
                            dto
                    );

            byte[] pdf =
                    pdfGeneratorPlaywrightService.generate(
                            HtmlNormalizerUtil.normalize(html)
                    );

            DocumentoInternoDTO arquivo =
                    new DocumentoInternoDTO(
                            slug + ".pdf",
                            "application/pdf",
                            pdf
                    );

            return documentoService.salvarInterno(
                    contexto,
                    referenciaId,
                    arquivo,
                    tipoDocumento
            );
        }

    public byte[] gerarPdfSemSalvar(
            String slug,
            Map<String,Object> dados
    ) {

        String html =
                documentoTemplateService
                        .gerar(
                                slug,
                                dados
                        );

        return pdfGeneratorPlaywrightService
                .generate(
                        HtmlNormalizerUtil.normalize(html)
                );
    }
    }
