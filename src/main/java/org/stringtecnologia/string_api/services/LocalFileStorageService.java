package org.stringtecnologia.string_api.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.stringtecnologia.string_api.config.storage.StorageProperties;
import org.stringtecnologia.string_api.model.dto.documento.ArquivoSalvoDTO;
import org.stringtecnologia.string_api.model.interfaces.FileStorageService;
import org.stringtecnologia.string_api.util.exceptions.BusinessException;
import org.stringtecnologia.string_api.util.exceptions.CodeError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path root;
    private final StorageProperties properties;

    public LocalFileStorageService(StorageProperties properties) {
        this.properties = properties;

        try {
            this.root = Paths.get(properties.getPath());
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao inicializar storage", e);
        }
    }

//    @Override
//    public String salvar(Long adiantamentoId, MultipartFile file) {
//
//        validarArquivo(file);
//
//        try {
//            String nomeOriginal = file.getOriginalFilename();
//
//            if (nomeOriginal == null) {
//                throw new IllegalArgumentException("Nome do arquivo inválido");
//            }
//
//            nomeOriginal = limparNome(nomeOriginal);
//
//            String extensao = obterExtensao(nomeOriginal);
//            String nomeUnico = UUID.randomUUID() + extensao;
//
//            // 📂 pasta por adiantamento
//            Path pasta = root
//                    .resolve("adiantamento")
//                    .resolve(adiantamentoId.toString());
//
//            Files.createDirectories(pasta);
//
//            Path destino = pasta.resolve(nomeUnico);
//
//            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
//
//            // 🔥 salva caminho relativo
//            return "adiantamento/" + adiantamentoId + "/" + nomeUnico;
//
//        } catch (IOException e) {
//            throw new RuntimeException("Erro ao salvar arquivo", e);
//        }
//    }


    @Override
    public ArquivoSalvoDTO salvar(Long adiantamentoId,
                                  MultipartFile file) {

        validarArquivo(file);

        try {

            String nomeOriginal =
                    Objects.requireNonNull(
                            file.getOriginalFilename()
                    );

            nomeOriginal = limparNome(nomeOriginal);

            String extensao =
                    obterExtensao(nomeOriginal);

            String nomeUnico =
                    UUID.randomUUID() + extensao;

            Path pasta = root
                    .resolve("adiantamento")
                    .resolve(adiantamentoId.toString());

            Files.createDirectories(pasta);

            Path destino = pasta.resolve(nomeUnico);

            // conteúdo em memória
            byte[] conteudo = file.getBytes();

            // hash SHA-256
            MessageDigest md =
                    MessageDigest.getInstance("SHA-256");

            byte[] digest = md.digest(conteudo);

            String hashSha256 =
                    HexFormat.of().formatHex(digest);

            // salva arquivo
            Files.write(destino, conteudo);

            String caminhoRelativo =
                    "adiantamento/" +
                            adiantamentoId +
                            "/" +
                            nomeUnico;

            return new ArquivoSalvoDTO(
                    caminhoRelativo,
                    nomeOriginal,
                    file.getContentType(),
                    file.getSize(),
                    hashSha256,
                    "SHA-256"
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao salvar arquivo",
                    e
            );
        }
    }





    @Override
    public byte[] carregar(String caminho) {
        try {
            Path path = root.resolve(caminho);

            // Verifica se o arquivo físico existe no diretório
            if (!Files.exists(path)) {
                throw new BusinessException(
                        CodeError.DOCUMENTO_NAO_ENCONTRADO,
                        "Arquivo físico não encontrado no storage: " + caminho
                );
            }

            return Files.readAllBytes(path);

        } catch (IOException e) {
            // Erros de permissão, disco cheio ou falha de I/O
            throw new BusinessException(
                    CodeError.ERRO_INTEGRACAO,
                    "Falha técnica ao acessar o arquivo no disco: " + e.getMessage()
            );
        }
    }

    @Override
    public void deletar(String caminho) {
        try {
            Path path = root.resolve(caminho);

            // deleta o arquivo primeiro
            Files.deleteIfExists(path);

            // pega a pasta do arquivo
            Path pasta = path.getParent();

            // tenta limpar se estiver vazia
            if (pasta != null) {
                limparPastaSeVazia(pasta);
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar arquivo", e);
        }
    }

    private void validarArquivo(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio");
        }

        // tamanho
        if (file.getSize() > properties.getMaxSize().toBytes()) {
            throw new IllegalArgumentException("Arquivo excede tamanho máximo permitido");
        }

        // tipo (content-type)
        String contentType = file.getContentType();

        if (contentType == null || !properties.getAllowedTypes().contains(contentType)) {
            throw new BusinessException(
                    CodeError.ARQUIVO_TIPO_NAO_PERMITIDO,
                    "Tipo de arquivo não permitido"
            );
        }

        // extensão (camada extra de segurança)
        String nome = file.getOriginalFilename();
        if (nome == null || !extensaoPermitida(nome)) {
            throw new IllegalArgumentException("Extensão de arquivo não permitida");
        }
    }

    private String limparNome(String nome) {
        return nome.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }

    private String obterExtensao(String nome) {
        int i = nome.lastIndexOf(".");
        return (i >= 0) ? nome.substring(i) : "";
    }

    private boolean extensaoPermitida(String nome) {
        String lower = nome.toLowerCase();
        return lower.endsWith(".pdf") ||
                lower.endsWith(".png") ||
                lower.endsWith(".jpg") ||
                lower.endsWith(".jpeg");
    }

    private void limparPastaSeVazia(Path pasta) throws IOException {

        if (!Files.exists(pasta)) return;

        try (var stream = Files.list(pasta)) {
            if (!stream.findAny().isPresent()) {
                Files.delete(pasta);
            }
        }
    }

//    @Override
//    public String salvarInterno(
//            Long adiantamentoId,
//            String nomeArquivo,
//            byte[] bytes
//    ) {
//        try {
//            // 1. Limpa o nome e obtém a extensão (seguindo a lógica do salvar original)
//            String nomeLimpo = limparNome(nomeArquivo);
//            String extensao = obterExtensao(nomeLimpo);
//
//            // 2. Gera o nome único (Padrão: UUID + .extensao)
//            // Removido o "- + nomeArquivo" para manter a consistência
//            String nomeUnico = UUID.randomUUID() + extensao;
//
//            // 3. Define a pasta por adiantamento
//            Path diretorio = root
//                    .resolve("adiantamento")
//                    .resolve(adiantamentoId.toString());
//
//            Files.createDirectories(diretorio);
//
//            // 4. Define o destino final
//            Path destino = diretorio.resolve(nomeUnico);
//
//            // 5. Grava os bytes no arquivo
//            Files.write(destino, bytes);
//
//            // 6. Retorna o caminho relativo consistente
//            return "adiantamento/" + adiantamentoId + "/" + nomeUnico;
//
//        } catch (Exception e) {
//            throw new RuntimeException("Erro ao salvar documento interno", e);
//        }
//    }

    @Override
    public ArquivoSalvoDTO salvarInterno(
            String contexto,
            Long adiantamentoId,
            String nomeArquivo,
            byte[] bytes
    ) {

        try {

            nomeArquivo = limparNome(nomeArquivo);

            String extensao =
                    obterExtensao(nomeArquivo);

            String nomeUnico =
                    UUID.randomUUID() + extensao;

            Path pasta = root
                    .resolve(contexto)
                    .resolve(adiantamentoId.toString());

            Files.createDirectories(pasta);

            Path destino = pasta.resolve(nomeUnico);

            // =====================================================
            // HASH SHA-256
            // =====================================================

            MessageDigest md =
                    MessageDigest.getInstance("SHA-256");

            byte[] digest = md.digest(bytes);

            String hashSha256 =
                    HexFormat.of().formatHex(digest);

            // =====================================================
            // SALVA
            // =====================================================

            Files.write(destino, bytes);

            String caminhoRelativo =
                    "adiantamento/" +
                            adiantamentoId +
                            "/" +
                            nomeUnico;

            return new ArquivoSalvoDTO(
                    caminhoRelativo,
                    nomeArquivo,
                    "application/pdf",
                    (long) bytes.length,
                    hashSha256,
                    "SHA-256"
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao salvar documento interno",
                    e
            );
        }
    }
}