# API de Mercado (Spring Boot 3) - Gerenciamento de Produtos

API REST de exemplo para gerenciar produtos de um mercado.

## Sumário

- [Descrição](#descrição)
- [Tecnologias](#tecnologias)
- [Arquitetura e pacotes](#arquitetura-e-pacotes)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do projeto](#configuração-do-projeto)
- [Executando localmente](#executando-localmente)
- [Documentação automática (OpenAPI/Swagger)](#documentação-automática-openapiswagger)
- [Modelo de dados](#modelo-de-dados)
- [Endpoints da API](#endpoints-da-api)
- [Exemplos de uso (JSON + curl)](#exemplos-de-uso-json--curl)
- [Tratamento de erros](#tratamento-de-erros)
- [Banco de dados (H2)](#banco-de-dados-h2)
- [Trocar para MySQL (produção)](#trocar-para-mysql-produção)
- [Scripts de Git (SSH)](#scripts-de-git-ssh)
- [Boas práticas e próximos passos](#boas-práticas-e-próximos-passos)

---

## Descrição

Esta API fornece operações CRUD para a entidade `Product` (Produto), com separação de camadas:

- **Controller**: expõe os endpoints REST
- **Service**: regras de negócio e orquestração
- **Repository**: acesso a dados com Spring Data JPA
- **Model**: entidade persistível (`Product`)
- **Exception Handler**: padroniza respostas de erro

---

## Tecnologias

- Java 17+
- Spring Boot 3.x
- Maven
- Spring Web
- Spring Data JPA
- H2 Database (memória)
- springdoc-openapi (Swagger)

---

## Arquitetura e pacotes

A estrutura esperada é:

```text
src/main/java/com/Captando/demo/
  DemoApplication.java
  controller/
    ProductController.java
  model/
    Product.java
  repository/
    ProductRepository.java
  service/
    ProductService.java
    ProductServiceImpl.java
    ProductNotFoundException.java
  exception/
    GlobalExceptionHandler.java
```

Arquivos de configuração:

```text
src/main/resources/
  application.properties
```

---

## Pré-requisitos

- Java 17 instalado
- Maven 3.9+
- Git
- Acesso SSH configurado no GitHub (opcional para clone/push)

Verifique:

```bash
java -version
mvn -version
git --version
ssh -T git@github.com
```

`ssh -T git@github.com` deve retornar sucesso (algo como `Hi <usuario>!`).

---

## Configuração do projeto

Se ainda não estiver com o projeto no seu ambiente:

```bash
git clone git@github.com:Captando/APISpring.git
cd APISpring
```

Ou, se já estiver no repositório, rode:

```bash
git fetch
git checkout main
git pull
```

---

## Executando localmente

### Executar com Maven

```bash
cd /Users/victorpcsca/Documents/APISpring
mvn spring-boot:run
```

A aplicação sobe em:

- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Dica: build rápido sem rodar

```bash
mvn clean package -DskipTests
```

Em seguida, subir via jar:

```bash
java -jar target/mercado-api-0.0.1-SNAPSHOT.jar
```

---

## Documentação automática (OpenAPI/Swagger)

A dependência `springdoc-openapi-starter-webmvc-ui` gera a documentação em tempo real.

- UI: `http://localhost:8080/swagger-ui.html`
- JSON da especificação: `http://localhost:8080/v3/api-docs`

---

## Modelo de dados

Entidade `Product`:

```json
{
  "id": 1,
  "name": "Arroz",
  "description": "Arroz branco 5kg",
  "price": 29.90
}
```

Campos:

- `id` (`Long`) – gerado pelo banco
- `name` (`String`) – nome do produto
- `description` (`String`) – descrição curta
- `price` (`double`) – preço

---

## Endpoints da API

Base path: `/products`

### `GET /products`

Lista todos os produtos.

### `GET /products/{id}`

Retorna um produto por id.

### `POST /products`

Cria um novo produto.

### `PUT /products/{id}`

Atualiza produto existente.

### `DELETE /products/{id}`

Remove produto existente.

---

## Exemplos de uso (JSON + curl)

> Em todos os exemplos, altere `localhost:8080` conforme necessário.

### 1) Criar produto

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Arroz",
    "description": "Arroz branco 5kg",
    "price": 29.9
  }'
```

### 2) Listar produtos

```bash
curl -X GET http://localhost:8080/products
```

### 3) Buscar por id

```bash
curl -X GET http://localhost:8080/products/1
```

### 4) Atualizar produto

```bash
curl -X PUT http://localhost:8080/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Arroz Premium",
    "description": "Arroz branco alto rendimento 5kg",
    "price": 31.5
  }'
```

### 5) Deletar produto

```bash
curl -X DELETE http://localhost:8080/products/1
```

Resposta esperada: `204 No Content`.

---

## Tratamento de erros

A API retorna erros de forma estruturada.

Exemplo de produto não encontrado:

```json
{
  "timestamp": "2026-02-14T14:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Produto não encontrado com id: 999"
}
```

Em erro interno, o retorno é 500 com objeto parecido (`timestamp`, `status`, `error`, `message`).

---

## Banco de dados (H2)

`application.properties` está configurado para H2 em memória:

```properties
spring.datasource.url=jdbc:h2:mem:mercadodb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

Console H2:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:mercadodb`
- User: `sa`
- Senha: (vazia)

> Se o schema não existir na primeira chamada, faça uma inserção via endpoint para inicializar.

---

## Trocar para MySQL (produção)

Para ambiente real, troque as propriedades (exemplo):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mercado_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=senha
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

Depois ajuste `spring.jpa.hibernate.ddl-auto` conforme sua política (normalmente `validate` ou `update`).

---

## Scripts de Git (SSH)

Se você usa SSH no macOS, configure remoto assim:

```bash
git remote add origin git@github.com:Captando/APISpring.git
# ou, se já existir:
git remote set-url origin git@github.com:Captando/APISpring.git
```

Fluxo rápido para publicar mudanças:

```bash
git add README.md

git commit -m "docs: atualizar README com instruções completas"

git push -u origin main
```

---

## Boas práticas e próximos passos

- Adicionar validação de payload:
  - `@NotBlank` em `name`
  - `@Size` e `@Positive` em `price`
- Mapear erros de validação com `@ControllerAdvice`
- Adicionar testes (`@SpringBootTest`, `MockMvc`) para todos os endpoints
- Criar perfil `application-dev.properties` e `application-prod.properties`
- Migrar para MySQL/PostgreSQL em produção

---

Projeto pronto para estudos e evolução para ambiente real de mercado (cadastro de produtos, estoque, categorias, autenticação, etc.).
