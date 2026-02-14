# API de Mercado (Produto)

Projeto de exemplo de **API REST em Java com Spring Boot 3** para gerenciar produtos de um mercado.

## Estrutura de pacotes

- `com.Captando.demo`
- `com.Captando.demo.controller`
- `com.Captando.demo.model`
- `com.Captando.demo.repository`
- `com.Captando.demo.service`
- `com.Captando.demo.exception`

## Requisitos

- Java 17+
- Maven 3.9+

## Como rodar localmente

```bash
cd /Users/victorpcsca/Documents/APISpring
mvn spring-boot:run
```

A API ficará em: `http://localhost:8080`

A documentação Swagger fica em:
`http://localhost:8080/swagger-ui.html`

## Endpoints da API

### GET /products

Lista todos os produtos.

```bash
curl -X GET http://localhost:8080/products
```

Resposta:

```json
[
  {
    "id": 1,
    "name": "Arroz Branco",
    "description": "Pacote de arroz 5kg",
    "price": 29.9
  }
]
```

### GET /products/{id}

Busca produto por ID.

```bash
curl -X GET http://localhost:8080/products/1
```

Resposta:

```json
{
  "id": 1,
  "name": "Arroz Branco",
  "description": "Pacote de arroz 5kg",
  "price": 29.9
}
```

### POST /products

Cria um novo produto.

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Feijão","description":"Feijão Carioca 1kg","price":11.5}'
```

Resposta (201):

```json
{
  "id": 2,
  "name": "Feijão",
  "description": "Feijão Carioca 1kg",
  "price": 11.5
}
```

### PUT /products/{id}

Atualiza um produto.

```bash
curl -X PUT http://localhost:8080/products/2 \
  -H "Content-Type: application/json" \
  -d '{"name":"Feijão Premium","description":"Feijão Carioca 1kg","price":12.9}'
```

Resposta:

```json
{
  "id": 2,
  "name": "Feijão Premium",
  "description": "Feijão Carioca 1kg",
  "price": 12.9
}
```

### DELETE /products/{id}

Remove um produto.

```bash
curl -X DELETE http://localhost:8080/products/2
```

Resposta: `204 No Content`

## Tratamento de erro

Exemplo quando ID não existe:

```json
{
  "timestamp": "2026-02-14T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Produto não encontrado com id: 999"
}
```
