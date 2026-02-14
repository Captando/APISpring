# API de Mercado (Spring Boot 3) - Java App (sem web frontend)

Projeto completo de API REST com uma **interface Java local (CLI)** para operar o mercado.

Autor: **Captando**  
Tecnologia: Java 17 + Spring Boot 3 + Spring Data JPA + H2 + springdoc-openapi

## Estrutura de pacotes

```text
src/main/java/com/Captando/demo/
  DemoApplication.java
  client/
    MarketConsoleApp.java
  controller/
    ProductController.java
  dto/
    ProductRequest.java
    ProductResponse.java
    StockAdjustmentRequest.java
  exception/
    ApiError.java
    GlobalExceptionHandler.java
  model/
    Product.java
  repository/
    ProductRepository.java
  service/
    ProductService.java
    ProductServiceImpl.java
    ProductNotFoundException.java
    InsufficientStockException.java
```

## Como rodar a API

```bash
cd /Users/victorpcsca/Documents/APISpring
mvn spring-boot:run
```

A API fica em `http://localhost:8080`.

## Como rodar a app Java (interface local)

Em outro terminal:

```bash
cd /Users/victorpcsca/Documents/APISpring
mvn exec:java -Dexec.mainClass=com.Captando.demo.client.MarketConsoleApp
```

Também pode customizar a URL da API:

```bash
mvn exec:java -Dapi.base.url=http://localhost:8080 -Dexec.mainClass=com.Captando.demo.client.MarketConsoleApp
```

## Endpoints

Base path: `/products` e `/api/v1/products`.

### GET `/products`

Listar com paginação/filtros:
- `name`, `category`, `minPrice`, `maxPrice`, `active`
- `page`, `size`, `sort`

Exemplo:

```bash
curl http://localhost:8080/products?page=0&size=10&sort=id,asc
```

### GET `/products/{id}`

```bash
curl http://localhost:8080/products/1
```

### POST `/products`

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Arroz",
    "description":"Arroz branco 5kg",
    "price":29.9,
    "category":"Mercearia",
    "stockQuantity":30,
    "active":true
  }'
```

### PUT `/products/{id}`

```bash
curl -X PUT http://localhost:8080/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Arroz Premium",
    "description":"Arroz parboilizado 5kg",
    "price":34.5,
    "category":"Mercearia",
    "stockQuantity":28,
    "active":true
  }'
```

### PATCH `/products/{id}/stock`

```bash
curl -X PATCH http://localhost:8080/products/1/stock \
  -H "Content-Type: application/json" \
  -d '{"delta":-2}'
```

### DELETE `/products/{id}`

```bash
curl -X DELETE http://localhost:8080/products/1
```

## OpenAPI e erros

- Swagger: `http://localhost:8080/swagger-ui.html`
- JSON API: `http://localhost:8080/v3/api-docs`
- Console H2: `http://localhost:8080/h2-console`

Resposta de erro padrão (`ApiError`):

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

## Banco de dados

`application.properties` está com H2 em memória:

```properties
spring.datasource.url=jdbc:h2:mem:mercadodb
```

Troque para MySQL em produção se necessário (já está documentado no próprio arquivo).

## Tags e organização para produção

- `ProductController` já usa tag OpenAPI: **Produtos** (documentação organizada no Swagger).
- Você pode evoluir com tags de negócio:
  - `Produtos` (CRUD)
  - `Estoque` (ajuste de quantidades)
  - `Saúde` (healthcheck, métricas)
