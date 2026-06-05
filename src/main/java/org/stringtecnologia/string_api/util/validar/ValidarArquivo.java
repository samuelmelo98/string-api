package org.stringtecnologia.string_api.util.validar;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.stringtecnologia.string_api.config.storage.StorageProperties;
import org.stringtecnologia.string_api.util.exceptions.BusinessException;
import org.stringtecnologia.string_api.util.exceptions.CodeError;

@Component
@RequiredArgsConstructor
public class ValidarArquivo {

    private final StorageProperties properties;

    /**
     * Executa todas as validações de segurança e negócio para o arquivo.
     */
    public void executar(MultipartFile file) {
        validarPresenca(file);
        validarTamanho(file);
        validarTipoMime(file.getContentType());
        validarExtensao(file.getOriginalFilename());
    }

    private void validarPresenca(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(CodeError.ARQUIVO_VAZIO, "Arquivo vazio");
        }
    }

    private void validarTamanho(MultipartFile file) {
        if (file.getSize() > properties.getMaxSize().toBytes()) {
            throw new BusinessException(
                    CodeError.ARQUIVO_TAMANHO_EXCEDIDO,
                    "Arquivo excede o tamanho máximo de " + properties.getMaxSize()
            );
        }
    }

    private void validarTipoMime(String contentType) {
        if (contentType == null || !properties.getAllowedTypes().contains(contentType)) {
            throw new BusinessException(
                    CodeError.ARQUIVO_TIPO_NAO_PERMITIDO,
                    "Tipo de conteúdo (MIME type) não permitido: " + contentType
            );
        }
    }

    private void validarExtensao(String nome) {
        if (nome == null || !nome.contains(".")) {
            throw new BusinessException(CodeError.ARQUIVO_EXTENSAO_INVALIDA, "Extensão ausente");
        }

        String extensao = nome.substring(nome.lastIndexOf(".") + 1).toLowerCase();

        if (!properties.getAllowedExtensions().contains(extensao)) {
            throw new BusinessException(
                    CodeError.ARQUIVO_EXTENSAO_INVALIDA,
                    "Extensão ." + extensao + " não permitida"
            );
        }
    }
}