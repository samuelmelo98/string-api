//package org.stringtecnologia.string_api.services;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class GeneratedDocumentService {
//
//    private final DocumentService templateService;
//    private final PdfGeneratorService pdfGeneratorService;
//    private final DocumentService documentoService;
//
//    public DocumentoResponseDTO gerarTermoAdiantamento(
//            Long adiantamentoId
//    ) {
//
//        // 1. Renderiza HTML
//        String html = templateService.generateDocument(
//                "termo-adiantamento",
//                adiantamentoId
//        );
//
//        // 2. Gera PDF
//        byte[] pdf = pdfGeneratorService.generate(html);
//
//        // 3. Converte para MultipartFile fake
//        MultipartFile file = new MockMultipartFile(
//                "file",
//                "termo-adiantamento.pdf",
//                "application/pdf",
//                pdf
//        );
//
//        // 4. Salva usando service existente
//        return documentoService.upload(
//                adiantamentoId,
//                file,
//                TipoDocumentoDominio.TERMO_ADIANTAMENTO_PARA_ASSINAR
//        );
//    }
//}
