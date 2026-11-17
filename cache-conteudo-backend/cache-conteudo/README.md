# API de Conteúdos com Cache

Projeto acadêmico simples em Java 21 e Spring Boot 4.0.1.

## Requisitos

- JDK 21 ou superior
- Maven 3.9 ou superior

## Executar

Na pasta do projeto:

```bash
mvn spring-boot:run
```

A API inicia em `http://localhost:8080`.

## Configuração do cache

No arquivo `src/main/resources/application.properties`:

```properties
app.cache.max-itens=5
app.cache.ttl-segundos=60
```

## Endpoints

- `POST /conteudos` — cadastra conteúdo
- `GET /conteudos/{id}` — busca conteúdo e informa `X-Cache: HIT` ou `MISS`
- `PUT /conteudos/{id}` — atualiza e invalida o cache
- `DELETE /conteudos/{id}` — exclui e invalida o cache
- `GET /cache/estatisticas` — consulta estatísticas
- `DELETE /cache` — limpa o cache

## Exemplo de cadastro

Envie um POST para `http://localhost:8080/conteudos` com:

```json
{
  "titulo": "Introdução ao Java",
  "corpo": "Java é uma linguagem de programação.",
  "autor": "João"
}
```

Cadastre pelo menos sete conteúdos para demonstrar a remoção LRU.

## Observações

- A primeira busca no banco tem atraso artificial de aproximadamente 2 segundos.
- O cache utiliza `ConcurrentHashMap`, TTL e remoção LRU.
- Os logs HIT/MISS e o tempo de cada busca foram mantidos porque são requisitos explícitos da atividade.
- O banco H2 é em memória: os dados são perdidos quando a aplicação é encerrada.
- A demonstração de expiração exige aguardar o TTL configurado.
