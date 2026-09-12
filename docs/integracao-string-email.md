# Módulo de e-mail da string-api

A `string-api` utiliza o serviço interno `string-emails` por OAuth2 `client_credentials`.

## Uso no código

Injete `StringEmailAdapter`:

```java
private final StringEmailAdapter stringEmailAdapter;
```

Envio de texto:

```java
stringEmailAdapter.enviarTexto(
        List.of("usuario@exemplo.com"),
        "Assunto",
        "Mensagem"
);
```

Envio HTML:

```java
stringEmailAdapter.enviarHtml(
        List.of("usuario@exemplo.com"),
        "Assunto",
        "<h1>Mensagem</h1>"
);
```

O retorno é `EmailEnvioResponseDTO`, com `status`, quantidade de destinatários, quantidade de anexos e `requestId`.

## Variáveis

```text
STRING_EMAIL_URL=http://string-email:8080
STRING_EMAIL_TOKEN_URL=https://auth.stringtecnologiadf.org/realms/stringtecnologia/protocol/openid-connect/token
STRING_EMAIL_CLIENT_ID=string-api-email
STRING_EMAIL_CLIENT_SECRET=<secret>
STRING_EMAIL_CONNECT_TIMEOUT=5s
STRING_EMAIL_READ_TIMEOUT=15s
STRING_EMAIL_TOKEN_REFRESH_SKEW=30s
```

Somente `STRING_EMAIL_CLIENT_SECRET` é segredo e deve vir de Kubernetes Secret/SealedSecret.

## Kubernetes

O Pod precisa da label:

```yaml
spec:
  template:
    metadata:
      labels:
        string-email-client: "true"
```

A NetworkPolicy da `string-emails` usa essa label para liberar TCP/8080.

## Token

O token é obtido no Keycloak usando `client_credentials`, mantido em cache em memória e renovado antes do vencimento. O módulo nunca registra token, client secret, destinatários nem conteúdo da mensagem.

O client `string-api-email` deve manter:

```text
aud contém email-api
role email-api/email.send
Access Token Lifespan = 120 s
```

## Observação sobre retry

O módulo não repete automaticamente um POST de e-mail após falha, evitando risco de duplicidade. Se ocorrer `401`, o token cacheado é invalidado para que a próxima chamada obtenha um token novo.

## Alteração necessária no GitOps da string-api

No Deployment da `string-api`, adicione a label no template do Pod e as variáveis não sensíveis:

```yaml
spec:
  template:
    metadata:
      labels:
        string-email-client: "true"
    spec:
      containers:
        - name: string-api
          env:
            - name: STRING_EMAIL_URL
              value: "http://string-email:8080"
            - name: STRING_EMAIL_TOKEN_URL
              value: "https://auth.stringtecnologiadf.org/realms/stringtecnologia/protocol/openid-connect/token"
            - name: STRING_EMAIL_CLIENT_ID
              value: "string-api-email"
            - name: STRING_EMAIL_CLIENT_SECRET
              valueFrom:
                secretKeyRef:
                  name: string-api-secret
                  key: string-email-client-secret
```

No `string-api-secret`, inclua a chave `string-email-client-secret` via SealedSecret. O valor em claro não deve ser salvo no Git.

Depois da atualização do Secret/Deployment:

```bash
kubectl -n prod rollout status deployment/string-api --timeout=180s
kubectl -n prod get pods -l app=string-api --show-labels
```

Confirme que o Pod mostra `string-email-client=true`.

## Dependências

O módulo reutiliza `RestClient`, `HttpServiceProxyFactory` e a infraestrutura HTTP já presente no projeto. Não foi adicionada dependência Maven nova.
