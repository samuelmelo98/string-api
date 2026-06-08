package org.stringtecnologia.string_api.model.interfaces;


import org.springframework.web.multipart.MultipartFile;
import org.stringtecnologia.string_api.model.dto.documento.ArquivoSalvoDTO;

public interface FileStorageService {

    ArquivoSalvoDTO salvar(Long adiantamentoId, MultipartFile file);

    byte[] carregar(String caminho);

    void deletar(String caminho);

    ArquivoSalvoDTO salvarInterno(
            String contexto,
            Long adiantamentoId,
            String nomeArquivo,
            byte[] bytes
    );
}

