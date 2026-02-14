# API de Mercado (Spring Boot 3) - Product CRUD

API REST de exemplo para gerenciar produtos de um mercado.

## Sumário

- [Descrição](#descrição)
- [Tecnologias](#tecnologias)
- [Arquitetura e pacotes](#arquitetura-e-pacotes)
- [Pré-requisitos](#pré-requisitos)
- [Executando localmente](#executando-localmente)
- [Documentação](#documentação)
- [Modelo de dados](#modelo-de-dados)
- [Endpoints da API](#endpoints-da-api)
- [Paginação e filtros](#paginação-e-filtros)
- [Validação](#validação)
- [Exemplos de uso (JSON + curl)](#exemplos-de-uso-json--curl)
- [Tratamento de erros](#tratamento-de-erros)
- [Banco de dados (H2)](#banco-de-dados-h2)
- [Trocar para MySQL](#trocar-para-mysql)

## Descrição

API REST de mercado com separação de camadas e contrato de produção:

- Controller: exposição de endpoints
- Service: regras de negócio
- Repository: acesso a dados com Spring Data JPA
- Model/DTO: entidade e contratos de request/response
- Exception handler: erros padronizados

## Tecnologias

- Java 17+
- Spring Boot 3.3.x
- Maven
- Spring Web
- Spring Data JPA
- Validation (`spring-boot-starter-validation`)
- H2 Database
- springdoc-openapi (Swagger)

## Arquitetura e pacotes

```text
src/main/java/com/Captando/demo/
  DemoApplication.java
  controller/
    ProductController.java
  model/
    Product.java
  dto/
    ProductRequest.java
    ProductResponse.java
  repository/
    ProductRepository.java
  service/
    ProductService.java
    ProductServiceImpl.java
    ProductNotFoundException.java
  exception/
    ApiError.java
    GlobalExceptionHandler.java

src/main/resources/
  application.properties
```

## Pré-requisitos

- Java 17
- Maven 3.9+
- Git
- Chave SSH configurada para GitHub (opcional)

## Executando localmente

```bash
cd /Users/victorpcsca/Documents/APISpring
mvn spring-boot:run
```

A API sobe em: `http://localhost:8080`

## Documentação

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Modelo de dados

`Product`:

```json
{
  "id": 1,
  "name": "Arroz",
  "description": "Arroz branco 5kg",
  "price": 29.9
}
```

Contrato de request (`POST` / `PUT`):

```json
{
  "name": "Arroz",
  "description": "Arroz branco 5kg",
  "price": 29.9
}
```

## Endpoints da API

Base path: `/products`

- `GET /products`: listar com paginação/filtro/ordenação
- `GET /products/{id}`: buscar por id
- `POST /products`: criar
- `PUT /products/{id}`: atualizar
- `DELETE /products/{id}`: remover

## Paginação e filtros

`GET /products` aceita query params:

- `name`: busca parcial por nome (`name=arroz`)
- `page`: página (padrão `0`)
- `size`: itens por página (padrão `10`)
- `sort`: ordenação (`id,asc`, `price,desc`)

Exemplo:

```bash
curl -X GET "http://localhost:8080/products?name=arroz&page=0&size=5&sort=price,desc"
```

## Validação

`ProductRequest` valida:

- `name`: obrigatório, 2 a 120 caracteres
- `price`: obrigatório, > 0
- `description`: opcional, até 500 caracteres

## Exemplos de uso (JSON + curl)

### Criar

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Feijão","description":"Feijão carioca 1kg","price":11.5}'
```

### Listar

```bash
curl -X GET http://localhost:8080/products
```

### Buscar por id

```bash
curl -X GET http://localhost:8080/products/1
```

### Atualizar

```bash
curl -X PUT http://localhost:8080/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Feijão Premium","description":"Feijão carioca 1kg","price":12.9}'
```

### Deletar

```bash
curl -X DELETE http://localhost:8080/products/1
```

Retorno esperado: `204 No Content`.

## Tratamento de erros

Formato padronizado (`ApiError`):

```json
{
  "timestamp": "2026-02-14T14:30:00",
  "status": 404,
  "code": "PRODUCT_NOT_FOUND",
  "message": "Produto não encontrado com id: 999",
  "path": "/products/999",
  "details": {}
}
```

Erro de validação:

```json
{
  "timestamp": "2026-02-14T14:30:00",
  "status": 400,
  "code": "INVALID_PAYLOAD",
  "message": "Dados inválidos",
  "path": "/products",
  "details": {
    "fields": {
      "name": "name é obrigatório",
      "price": "price deve ser maior que zero"
    }
  }
}
```

Erro interno: `500` com `code: INTERNAL_ERROR`.

## Banco de dados (H2)

`application.properties` usa:

```properties
spring.datasource.url=jdbc:h2:mem:mercadodb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

Console: `http://localhost:8080/h2-console`

## Trocar para MySQL

Comente/ajuste no `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mercado_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=senha
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```
