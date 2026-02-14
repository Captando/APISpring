# API de Mercado (Spring Boot 3) - Product API v2

API REST para catálogo de produtos com foco em uso real: filtros, paginação, controle de estoque e erro padronizado.

## Sumário

- [Arquitetura](#arquitetura)
- [Como rodar](#como-rodar)
- [Contratos](#contratos)
- [Endpoints](#endpoints)
- [Erros](#erros)
- [Tags git](#tags-git)

## Arquitetura

Pacotes:

```text
src/main/java/com/Captando/demo/
  DemoApplication.java
  controller/
    ProductController.java
  dto/
    ProductRequest.java
    ProductResponse.java
    StockAdjustmentRequest.java
  model/
    Product.java
  repository/
    ProductRepository.java
  service/
    ProductService.java
    ProductServiceImpl.java
    ProductNotFoundException.java
    InsufficientStockException.java
  exception/
    ApiError.java
    GlobalExceptionHandler.java
```

## Como rodar

```bash
cd /Users/victorpcsca/Documents/APISpring
mvn spring-boot:run
```

URLs úteis:

- API base: `http://localhost:8080`
- OpenAPI: `http://localhost:8080/v3/api-docs`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Console H2: `http://localhost:8080/h2-console`

## Contratos

`ProductRequest`:

```json
{
  "name": "Arroz",
  "description": "Arroz branco 5kg",
  "price": 29.9,
  "category": "Mercearia",
  "stockQuantity": 30,
  "active": true
}
```

`ProductResponse` inclui os mesmos campos + `id`.

`StockAdjustmentRequest`:

```json
{ "delta": 5 }
```

Pode ser positivo (entrada) ou negativo (baixa de estoque).

## Endpoints

Base path: `/products` e `/api/v1/products`.

### GET /products

Query params:

- `name` (opcional): busca parcial no nome
- `category` (opcional): categoria exata
- `minPrice` / `maxPrice` (opcional)
- `active` (opcional): `true|false`
- `page`, `size`, `sort` (padrões: `0`, `10`, `id,asc`)

### GET /products/{id}

Busca por ID.

### POST /products

Cria produto.

### PUT /products/{id}

Atualiza produto.

### PATCH /products/{id}/stock

Ajusta estoque via `delta`.

### DELETE /products/{id}

Remove produto.

## Erros (ApiError)

Exemplo not found:

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

Exemplo estoque insuficiente:

```json
{
  "timestamp": "2026-02-14T14:30:00",
  "status": 409,
  "code": "INSUFFICIENT_STOCK",
  "message": "Estoque insuficiente para o produto id 1. Disponível: 2, solicitado: -5",
  "path": "/products/1/stock",
  "details": {}
}
```

## Tags git

Para versionar esta evolução:

```bash
git tag -a v2.0.0 -m "Feat: filtros e controle de estoque"
git push origin v2.0.0
```
