package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.stringtecnologia.string_api.documento.dto.DocumentoInternoDTO;
import org.stringtecnologia.string_api.documento.dto.DocumentoResponseDTO;
import org.stringtecnologia.string_api.model.dto.documento.ArquivoSalvoDTO;
import org.stringtecnologia.string_api.model.dto.documento.DocumentoDownloadDTO;
import org.stringtecnologia.string_api.model.entities.Documento;
import org.stringtecnologia.string_api.model.entities.DominioSistema;
import org.stringtecnologia.string_api.model.interfaces.FileStorageService;
import org.stringtecnologia.string_api.repository.DocumentoRepository;
import org.stringtecnologia.string_api.util.TipoDocumentoDominio;
import org.stringtecnologia.string_api.util.exceptions.BusinessException;
import org.stringtecnologia.string_api.util.exceptions.CodeError;
import org.stringtecnologia.string_api.util.validar.ValidarArquivo;
import org.stringtecnologia.string_api.model.dto.*;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final DominioSistemaService dominioSistemaService;
    private final FileStorageService storage;
    private final ValidarArquivo validarArquivo;
    Object adiantamento = null;


//    public DocumentoResponseDTO upload(Long adiantamentoId, MultipartFile file, TipoDocumentoDominio tipoEnum) {
//        validarArquivo.executar(file);
//
//        String caminho = null;
//        try {
//            Adiantamento adiantamento = buscarAdiantamento(adiantamentoId);
//
//            removerDocumentoAnteriorSeNecessario(adiantamentoId, tipoEnum);
//
//            DominioSistema tipoDocumento = dominioSistemaService.buscarDominioCache(tipoEnum);
//
//            caminho = storage.salvar(adiantamentoId, file);
//
//            Documento documentoPersistido = salvarMetadadosDocumento(adiantamento, file, tipoDocumento, caminho);
//
//            return criarResponseDTO(documentoPersistido);
//
//        } catch (Exception e) {
//            efetuarRollbackArquivo(caminho);
//            throw new RuntimeException("Erro ao processar upload do documento: " + e.getMessage(), e);
//        }
//    }


    public DocumentoResponseDTO upload(
            Long adiantamentoId,
            MultipartFile file,
            TipoDocumentoDominio tipoEnum
    ) {

        validarArquivo.executar(file);

        ArquivoSalvoDTO arquivoSalvo = null;

        try {

//            Adiantamento adiantamento =
//                    buscarAdiantamento(adiantamentoId);

            removerDocumentoAnteriorSeNecessario(
                    adiantamentoId,
                    tipoEnum
            );

            DominioSistema tipoDocumento =
                    dominioSistemaService
                            .buscarDominioCache(tipoEnum);

            arquivoSalvo =
                    storage.salvar(adiantamentoId, file);

            Documento documentoPersistido =
                    salvarMetadadosDocumento(
                            null,
                            tipoDocumento,
                            arquivoSalvo
                    );

            return criarResponseDTO(documentoPersistido);

        } catch (Exception e) {

            if (arquivoSalvo != null) {
                efetuarRollbackArquivo(
                        arquivoSalvo.caminho()
                );
            }

            throw new RuntimeException(
                    "Erro ao processar upload do documento: "
                            + e.getMessage(),
                    e
            );
        }
    }



// --- Métodos de Apoio (Responsabilidades Únicas) ---

//    private Adiantamento buscarAdiantamento(Long adiantamentoId) {
//        return adiantamentoRepository.findById(adiantamentoId)
//                .orElseThrow(() -> new RuntimeException("Adiantamento não encontrado"));
//    }

    private void removerDocumentoAnteriorSeNecessario(Long adiantamentoId, TipoDocumentoDominio tipoEnum) {
//        if (tipoEnum == TipoDocumentoDominio.TERMO_ADIANTAMENTO_PARA_ASSINAR) {
//            documentoRepository.findFirstByAdiantamentoAdiantamentoIdAndTipoDocumentoCodigoOrderByDataCadastroDesc(
//                    adiantamentoId,
//                    tipoEnum.getCodigo()
//            ).ifPresent(docAntigo -> {
//                storage.deletar(docAntigo.getCaminho());
//                documentoRepository.delete(docAntigo);
//                documentoRepository.flush(); // Garante a deleção antes de inserir o novo
//            });
//        }
    }

//    private Documento salvarMetadadosDocumento(Adiantamento adiantamento, MultipartFile file,
//                                               DominioSistema tipo, String caminho) {
//        Documento doc = new Documento();
//        doc.setNome(file.getOriginalFilename());
//        doc.setMimeType(file.getContentType());
//        doc.setTamanho(file.getSize());
//        doc.setCaminho(caminho);
//        doc.setAdiantamento(adiantamento);
//        doc.setTipoDocumento(tipo);
//
//        return documentoRepository.save(doc);
//    }

    private Documento salvarMetadadosDocumento(
            Object adiantamento,
            DominioSistema tipo,
            ArquivoSalvoDTO arquivo
    ) {

        Documento doc = new Documento();

        doc.setNome(arquivo.nomeOriginal());

        doc.setMimeType(arquivo.mimeType());

        doc.setTamanho(arquivo.tamanho());

        doc.setCaminho(arquivo.caminho());

        doc.setHashSha256(
                arquivo.hashSha256()
        );

        doc.setAlgoritmoHash(
                arquivo.algoritmoHash()
        );

//        doc.setAdiantamento(adiantamento);

        doc.setTipoDocumento(tipo);

        return documentoRepository.save(doc);
    }

    private DocumentoResponseDTO criarResponseDTO(Documento doc) {
        return new DocumentoResponseDTO(
                doc.getDocumentoId(),
                doc.getNome(),
                doc.getMimeType(),
                doc.getTipoDocumento().getDescricao(),
                doc.getCaminho(),
                doc.getTamanho(),
                doc.getHashSha256()
        );
    }

    private void efetuarRollbackArquivo(String caminho) {
        if (caminho != null) {
            storage.deletar(caminho);
        }
    }

    public DocumentoDownloadDTO download(Long documentoId) {

        Documento doc = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new BusinessException(
                        CodeError.DOCUMENTO_NAO_ENCONTRADO,
                        "Registro do documento com ID " + documentoId + " não encontrado no sistema."
                ));

        byte[] dados = storage.carregar(doc.getCaminho());

        return new DocumentoDownloadDTO(
                doc.getNome(),
                doc.getMimeType(),
                doc.getTipoDocumento().getDescricao(),
                dados
        );
    }

    public void deletar(Long documentoId) {

        Documento doc = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado"));

        storage.deletar(doc.getCaminho());
        documentoRepository.delete(doc);
    }

    public List<DocumentoResponseDTO> listarPorAdiantamento(Long adiantamentoId) {

        return null;
//                documentoRepository.findByAdiantamentoAdiantamentoId(adiantamentoId)
//                .stream()
//                .map(doc -> new DocumentoResponseDTO(
//                        doc.getDocumentoId(),
//                        doc.getNome(),
//                        doc.getMimeType(),
//                        doc.getTipoDocumento().getDescricao(),
//                        doc.getTamanho(),
//                        doc.getHashSha256()
//                ))
//                .toList();
    }

    public DocumentoDownloadDTO buscarTermoParaAssinar(Long adiantamentoId) {

//        Documento doc = documentoRepository
//                .findFirstByAdiantamentoAdiantamentoIdAndTipoDocumentoCodigoOrderByDataCadastroDesc(
//                        adiantamentoId,
//                        TipoDocumentoDominio.TERMO_ADIANTAMENTO_PARA_ASSINAR.getCodigo()
//                )
//                .orElseThrow(() -> new RuntimeException("Termo para assinatura não encontrado"));

//        byte[] dados = storage.carregar(doc.getCaminho());
//
//        return new DocumentoDownloadDTO(
//                doc.getNome(),
//                doc.getMimeType(),
//                doc.getTipoDocumento().getDescricao(),
//                dados
//        );
       return  null;
    }

    @Transactional
    public List<DocumentoResponseDTO> uploadBatch(
            Long adiantamentoId,
            List<MultipartFile> files,
            List<TipoDocumentoDominio> tipos
    ) {

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException(
                    "Nenhum arquivo enviado"
            );
        }

        List<String> caminhosSalvos =
                new ArrayList<>();

        List<DocumentoResponseDTO> responses =
                new ArrayList<>();

        try {

//            Adiantamento adiantamento =
//                    buscarAdiantamento(adiantamentoId);

            for (int i = 0; i < files.size(); i++) {

                MultipartFile file = files.get(i);

                TipoDocumentoDominio tipoEnum =
                        (tipos != null && tipos.size() > i)
                                ? tipos.get(i)
                                : TipoDocumentoDominio.ANEXO;

                validarArquivo.executar(file);

                removerDocumentoAnteriorSeNecessario(
                        adiantamentoId,
                        tipoEnum
                );

                DominioSistema tipo =
                        dominioSistemaService
                                .buscarDominioCache(tipoEnum);

                // =====================================================
                // SALVA FÍSICO + HASH
                // =====================================================

                ArquivoSalvoDTO arquivoSalvo =
                        storage.salvar(
                                adiantamentoId,
                                file
                        );

                // rollback físico
                caminhosSalvos.add(
                        arquivoSalvo.caminho()
                );

                // =====================================================
                // PERSISTE METADADOS
                // =====================================================

                Documento doc =
                        salvarMetadadosDocumento(
                                null,
                                tipo,
                                arquivoSalvo
                        );

                responses.add(
                        criarResponseDTO(doc)
                );
            }

            return responses;

        } catch (Exception e) {

            // rollback físico
            caminhosSalvos.forEach(storage::deletar);

            throw new RuntimeException(
                    "Erro no upload batch: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Transactional
    public void uploadBatchInterno(
            Long adiantamentoId,
            List<MultipartFile> files
    ) {

        List<String> caminhosSalvos =
                new ArrayList<>();

        try {

//            Adiantamento adiantamento =
//                    buscarAdiantamento(adiantamentoId);

            for (MultipartFile file : files) {

                validarArquivo.executar(file);

                // =====================================================
                // SALVA FÍSICO + HASH
                // =====================================================

                ArquivoSalvoDTO arquivoSalvo =
                        storage.salvar(
                                adiantamentoId,
                                file
                        );

                // rollback físico
                caminhosSalvos.add(
                        arquivoSalvo.caminho()
                );

                // =====================================================
                // TIPO DOCUMENTO
                // =====================================================

                DominioSistema tipo =
                        dominioSistemaService
                                .buscarDominioCache(
                                        TipoDocumentoDominio.ANEXO
                                );

                // =====================================================
                // PERSISTE METADADOS
                // =====================================================

                salvarMetadadosDocumento(
                        null,
                        tipo,
                        arquivoSalvo
                );
            }

        } catch (Exception e) {

            // rollback físico
            caminhosSalvos.forEach(
                    storage::deletar
            );

            throw new RuntimeException(
                    "Erro ao salvar documentos",
                    e
            );
        }
    }

    @Transactional
    public DocumentoResponseDTO salvarInterno(
            Long adiantamentoId,
            DocumentoInternoDTO arquivo,
            TipoDocumentoDominio tipoEnum
    ) {

        ArquivoSalvoDTO arquivoSalvo = null;

        try {

            // =====================================================
            // BUSCA ADIANTAMENTO
            // =====================================================

//            Adiantamento adiantamento =
//                    buscarAdiantamento(adiantamentoId);

            // =====================================================
            // REMOVE DOCUMENTO ANTERIOR
            // =====================================================

            removerDocumentoAnteriorSeNecessario(
                    adiantamentoId,
                    tipoEnum
            );

            // =====================================================
            // BUSCA TIPO DOCUMENTO
            // =====================================================

            DominioSistema tipoDocumento =
                    dominioSistemaService
                            .buscarDominioCache(tipoEnum);

            // =====================================================
            // SALVA FÍSICO + HASH
            // =====================================================

            arquivoSalvo =
                    storage.salvarInterno(
                            adiantamentoId,
                            arquivo.nomeArquivo(),
                            arquivo.bytes()
                    );

            // =====================================================
            // MONTA ENTIDADE
            // =====================================================

            Documento documento = new Documento();

            documento.setNome(
                    arquivoSalvo.nomeOriginal()
            );

            documento.setMimeType(
                    arquivoSalvo.mimeType()
            );

            documento.setTamanho(
                    arquivoSalvo.tamanho()
            );

            documento.setCaminho(
                    arquivoSalvo.caminho()
            );

            documento.setHashSha256(
                    arquivoSalvo.hashSha256()
            );

            documento.setAlgoritmoHash(
                    arquivoSalvo.algoritmoHash()
            );

//            documento.setAdiantamento(
//                    adiantamento
//            );

            documento.setTipoDocumento(
                    tipoDocumento
            );

            // =====================================================
            // PERSISTE
            // =====================================================

            Documento salvo =
                    documentoRepository.save(documento);

            // =====================================================
            // RESPONSE
            // =====================================================

            return criarResponseDTO(salvo);

        } catch (Exception e) {

            if (arquivoSalvo != null) {
                efetuarRollbackArquivo(
                        arquivoSalvo.caminho()
                );
            }

            throw new RuntimeException(
                    "Erro ao salvar documento interno",
                    e
            );
        }
    }
}