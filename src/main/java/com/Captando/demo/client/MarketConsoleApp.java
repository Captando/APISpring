package com.Captando.demo.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

public class MarketConsoleApp {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static final String PRODUCTS_PATH = "/products";

    private final String baseUrl;
    private final HttpClient client;
    private final ObjectMapper mapper;
    private final Scanner scanner;

    public MarketConsoleApp() {
        this.baseUrl = System.getProperty("api.base.url", DEFAULT_BASE_URL);
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        this.mapper = new ObjectMapper();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        new MarketConsoleApp().run();
    }

    private void run() {
        while (true) {
            printMenu();
            String option = readLine("Escolha: ").trim();

            try {
                switch (option) {
                    case "1" -> listProducts();
                    case "2" -> getById();
                    case "3" -> createProduct();
                    case "4" -> updateProduct();
                    case "5" -> adjustStock();
                    case "6" -> deleteProduct();
                    case "0" -> {
                        System.out.println("Encerrando app. Até mais!");
                        return;
                    }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception ex) {
                System.out.println("Erro: " + ex.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n==== Mercado App (Java CLI) ====");
        System.out.println("1) Listar produtos");
        System.out.println("2) Buscar produto por id");
        System.out.println("3) Criar produto");
        System.out.println("4) Atualizar produto");
        System.out.println("5) Ajustar estoque");
        System.out.println("6) Remover produto");
        System.out.println("0) Sair");
    }

    private void listProducts() throws IOException, InterruptedException {
        String path = baseUrl + PRODUCTS_PATH;
        HttpResponse<String> response = sendRequest("GET", path, null);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }

        JsonNode root = mapper.readTree(response.body());
        List<ProductDto> products = mapper.convertValue(root.path("content"), new TypeReference<>() {});
        System.out.println("\nProdutos:");
        if (products == null || products.isEmpty()) {
            System.out.println("Nenhum produto encontrado.");
            return;
        }
        for (ProductDto product : products) {
            printProduct(product);
        }

        long total = root.path("totalElements").asLong(0);
        int page = root.path("number").asInt(0);
        int size = root.path("size").asInt(0);
        int totalPages = root.path("totalPages").asInt(0);
        System.out.printf("Página %d de %d | total=%d | tamanho=%d%n", page + 1, totalPages, total, size);
    }

    private void getById() throws IOException, InterruptedException {
        Long id = readLong("ID do produto: ");
        HttpResponse<String> response = sendRequest("GET", endpointById(id), null);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ProductDto product = mapper.readValue(response.body(), ProductDto.class);
        System.out.println("\nProduto encontrado:");
        printProduct(product);
    }

    private void createProduct() throws IOException, InterruptedException {
        ProductDto request = readProductData(null);
        HttpResponse<String> response = sendRequest("POST", baseUrl + PRODUCTS_PATH, request);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ProductDto created = mapper.readValue(response.body(), ProductDto.class);
        System.out.println("Produto criado:");
        printProduct(created);
    }

    private void updateProduct() throws IOException, InterruptedException {
        Long id = readLong("ID do produto a atualizar: ");
        System.out.println("Deixe em branco para manter o valor atual (quando possível).");
        ProductDto request = readProductData(null);
        HttpResponse<String> response = sendRequest("PUT", endpointById(id), request);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ProductDto updated = mapper.readValue(response.body(), ProductDto.class);
        System.out.println("Produto atualizado:");
        printProduct(updated);
    }

    private void adjustStock() throws IOException, InterruptedException {
        Long id = readLong("ID do produto: ");
        Integer delta = readInt("Delta (+ entrada, - saída): ");
        StockRequest request = new StockRequest(delta);
        HttpResponse<String> response = sendRequest("PATCH", endpointById(id) + "/stock", request);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ProductDto updated = mapper.readValue(response.body(), ProductDto.class);
        System.out.println("Estoque atualizado:");
        printProduct(updated);
    }

    private void deleteProduct() throws IOException, InterruptedException {
        Long id = readLong("ID do produto: ");
        HttpResponse<String> response = sendRequest("DELETE", endpointById(id), null);
        if (isSuccess(response.statusCode())) {
            System.out.println("Produto removido.");
        } else {
            printApiError(response);
        }
    }

    private ProductDto readProductData(ProductDto current) {
        String name = readLine("Nome: ");
        String description = readLine("Descrição: ");
        Double price = readDouble("Preço: ");
        String category = readLine("Categoria: ");
        Integer stockQuantity = readInt("Estoque inicial: ");
        Boolean active = readBoolean("Ativo? (s/n): ");
        return new ProductDto(
                null,
                fallback(name, current != null ? current.name : null),
                fallback(description, current != null ? current.description : null),
                price == null ? 0.0 : price,
                fallback(category, current != null ? current.category : null),
                stockQuantity == null ? 0 : stockQuantity,
                active == null ? true : active
        );
    }

    private HttpResponse<String> sendRequest(String method, String url, Object body)
            throws IOException, InterruptedException {

        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(5));

        switch (method) {
            case "GET" -> request.GET();
            case "DELETE" -> request.DELETE();
            case "POST", "PUT", "PATCH" -> {
                String payload;
                try {
                    payload = body == null ? "{}" : mapper.writeValueAsString(body);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Erro ao serializar corpo da requisição", e);
                }
                request.method(method, HttpRequest.BodyPublishers.ofString(payload));
            }
            default -> throw new IllegalArgumentException("Método não suportado: " + method);
        }

        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private boolean isSuccess(int status) {
        return status >= 200 && status < 300;
    }

    private void printApiError(HttpResponse<String> response) {
        try {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            System.out.printf("Falha %d [%s] em %s: %s%n",
                    error.status, error.code, error.message);
            if (error.details != null && !error.details.isEmpty()) {
                System.out.println("Detalhes: " + error.details);
            }
        } catch (IOException e) {
            System.out.println("Falha " + response.statusCode() + ": " + response.body());
        }
    }

    private void printProduct(ProductDto product) {
        String output = String.format(
                "#%d | %s | R$ %.2f | Estoque: %d | Ativo: %s | %s",
                product.id,
                product.name,
                product.price,
                product.stockQuantity,
                product.active ? "sim" : "não",
                product.description
        );
        System.out.println(output);
        if (product.category != null && !product.category.isBlank()) {
            System.out.println("  Categoria: " + product.category);
        }
    }

    private String endpointById(Long id) {
        return baseUrl + PRODUCTS_PATH + "/" + id;
    }

    private String readLine(String label) {
        System.out.print(label);
        return scanner.nextLine();
    }

    private Long readLong(String label) {
        while (true) {
            try {
                return Long.parseLong(readLine(label).trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private Integer readInt(String label) {
        while (true) {
            try {
                return Integer.parseInt(readLine(label).trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private Double readDouble(String label) {
        while (true) {
            try {
                String value = readLine(label).trim();
                return Double.parseDouble(value.replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Digite um valor numérico válido (ex: 12.90).");
            }
        }
    }

    private Boolean readBoolean(String label) {
        String value = readLine(label).trim().toLowerCase();
        if (value.isBlank()) {
            return null;
        }
        if ("s".equals(value) || "sim".equals(value) || "y".equals(value) || "yes".equals(value)) {
            return true;
        }
        if ("n".equals(value) || "nao".equals(value) || "não".equals(value) || "no".equals(value)) {
            return false;
        }
        System.out.println("Use s/n.");
        return readBoolean(label);
    }

    private String fallback(String input, String oldValue) {
        String trimmed = input == null ? null : input.trim();
        return (trimmed == null || trimmed.isBlank()) ? oldValue : trimmed;
    }

    private static class ProductDto {
        public Long id;
        public String name;
        public String description;
        public Double price;
        public String category;
        public Integer stockQuantity;
        public boolean active;

        public ProductDto() {
        }

        public ProductDto(Long id, String name, String description, Double price, String category, Integer stockQuantity, boolean active) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.category = category;
            this.stockQuantity = stockQuantity;
            this.active = active;
        }
    }

    private static class StockRequest {
        private Integer delta;

        public StockRequest(Integer delta) {
            this.delta = delta;
        }
    }

    private static class ApiError {
        public int status;
        public String code;
        public String message;
        public String path;
        public java.util.Map<String, Object> details;
    }
}
