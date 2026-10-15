# Serviços locais OCR — WSL2/Docker

1. Copie o diretório para dentro do projeto `C:\projetos\string-api\infra\ocr-local`.
2. No WSL, entre em `/mnt/c/projetos/string-api/infra/ocr-local`.
3. `cp .env.ocr.local.example .env.ocr.local` e edite a senha.
4. `docker compose --env-file .env.ocr.local -f compose.ocr.local.yml up -d`
5. `docker compose --env-file .env.ocr.local -f compose.ocr.local.yml ps`
6. `docker compose --env-file .env.ocr.local -f compose.ocr.local.yml logs minio-init`
7. Acesse a console MinIO em `http://localhost:9001`.

## Aplicação

Configuração para profile `dev` (separadamente em `application-dev.yml` ou em variáveis de ambiente da execução IntelliJ):

```yaml
minio:
  endpoint: http://localhost:9000
  bucket: string-api
  access-key: ${MINIO_ACCESS_KEY}
  secret-key: ${MINIO_SECRET_KEY}
ocr:
  storage:
    provider: minio
    prefix: ocr/imagens
  tessdata:
    path: ./tessdata
  worker:
    enabled: true
    delay-ms: 3000
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

Defina MINIO_ACCESS_KEY e MINIO_SECRET_KEY iguais a MINIO_LOCAL_USER e MINIO_LOCAL_PASSWORD, respectivamente, na configuração de ambiente da aplicação. Não versionar segredos. Não sobrescreva `application.yml` de produção.

## Testes

```bash
curl -i http://localhost:9000/minio/health/live
docker compose --env-file .env.ocr.local -f compose.ocr.local.yml exec redis redis-cli ping
```

Com `docker compose down`, os volumes continuam persistidos. **Não use `down -v`** se precisar preservar os dados locais.

Obs.: as tags `latest` facilitam o primeiro teste; após validar, fixe uma versão/digest do MinIO e MC. O PostgreSQL existente não faz parte deste compose.
