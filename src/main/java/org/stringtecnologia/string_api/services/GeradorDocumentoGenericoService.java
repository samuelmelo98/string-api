package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.documento.dto.DocumentoInternoDTO;
import org.stringtecnologia.string_api.documento.dto.DocumentoResponseDTO;
import org.stringtecnologia.string_api.util.HtmlNormalizerUtil;
import org.stringtecnologia.string_api.util.TipoDocumentoDominio;

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

    public <T> byte[] gerarPdfSemSalvar(
            String slug,
            T dto
    ) {

        String html =
                documentoTemplateService.gerar(
                        slug,
                        dto
                );

        return pdfGeneratorPlaywrightService.generate(
                HtmlNormalizerUtil.normalize(html)
        );
    }
    }
