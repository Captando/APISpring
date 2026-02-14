package com.Captando.demo.client;

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
    private static final String PRODUCT_PATH = "/products";
    private static final String COMANDA_PATH = "/comandas";
    private static final String APP_LOGO = """
     __  __      _            ____                  _     
    |  \\/  |__ _(_)_ _   _   / ___|  ___ _ ____   _(_)___ 
    | |\\/| / _` | | | | | | | |  _| / __| '_ \\ \\ / / / __|
    | |  | (_| | | | |_| | | |_| | (__| | | \\ V /| \\__ \\
    |_|  |_\\__,_|_|_|\\__, |  \\____|\\___|_| |_|\\_/ |_|___/
                    |___/   Mercado Captando
    """;

    private final String baseUrl;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final Scanner scanner = new Scanner(System.in);

    public MarketConsoleApp() {
        this.baseUrl = System.getProperty("api.base.url", DEFAULT_BASE_URL);
    }

    public static void main(String[] args) {
        new MarketConsoleApp().run();
    }

    private void run() {
        System.out.println(APP_LOGO);
        while (true) {
            printMenu();
            String choice = readLine("Escolha: ").trim();
            try {
                switch (choice) {
                    case "1" -> listProducts();
                    case "2" -> getProductById();
                    case "3" -> createProduct();
                    case "4" -> updateProduct();
                    case "5" -> adjustStock();
                    case "6" -> deleteProduct();
                    case "7" -> listComandas();
                    case "8" -> getComandaById();
                    case "9" -> createComanda();
                    case "10" -> addItemToComanda();
                    case "11" -> removeItemFromComanda();
                    case "12" -> closeComanda();
                    case "13" -> deleteComanda();
                    case "0" -> {
                        System.out.println("Encerrando app...");
                        return;
                    }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1) Listar produtos");
        System.out.println("2) Buscar produto por id");
        System.out.println("3) Criar produto");
        System.out.println("4) Atualizar produto");
        System.out.println("5) Ajustar estoque");
        System.out.println("6) Remover produto");
        System.out.println("7) Listar comandas");
        System.out.println("8) Buscar comanda por id");
        System.out.println("9) Criar comanda");
        System.out.println("10) Adicionar item na comanda");
        System.out.println("11) Remover item da comanda");
        System.out.println("12) Fechar comanda");
        System.out.println("13) Remover comanda");
        System.out.println("0) Sair");
    }

    private void listProducts() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("GET", baseUrl + PRODUCT_PATH + "?page=0&size=10&sort=id,asc", null);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        JsonNode root = mapper.readTree(response.body());
        List<ProductDto> products = mapper.convertValue(root.path("content"), mapper.getTypeFactory().constructCollectionType(List.class, ProductDto.class));
        System.out.println("\nProdutos:");
        if (products == null || products.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        products.forEach(this::printProduct);
        System.out.printf("Total: %d | Página: %d de %d%n",
                root.path("totalElements").asLong(0), root.path("number").asInt(0) + 1, root.path("totalPages").asInt(0));
    }

    private void getProductById() throws IOException, InterruptedException {
        Long id = readLong("ID do produto: ");
        HttpResponse<String> response = sendRequest("GET", endpoint(PRODUCT_PATH, id), null);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ProductDto product = mapper.readValue(response.body(), ProductDto.class);
        printProduct(product);
    }

    private void createProduct() throws IOException, InterruptedException {
        ProductDto request = readProductData();
        HttpResponse<String> response = sendRequest("POST", baseUrl + PRODUCT_PATH, request);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ProductDto created = mapper.readValue(response.body(), ProductDto.class);
        System.out.println("Criado:");
        printProduct(created);
    }

    private void updateProduct() throws IOException, InterruptedException {
        Long id = readLong("ID do produto: ");
        ProductDto request = readProductData();
        HttpResponse<String> response = sendRequest("PUT", endpoint(PRODUCT_PATH, id), request);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ProductDto updated = mapper.readValue(response.body(), ProductDto.class);
        System.out.println("Atualizado:");
        printProduct(updated);
    }

    private void adjustStock() throws IOException, InterruptedException {
        Long id = readLong("ID do produto: ");
        Integer delta = readInt("Delta do estoque (+ entrada, - saída): ");
        HttpResponse<String> response = sendRequest("PATCH", endpoint(PRODUCT_PATH, id) + "/stock", new StockRequest(delta));
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
        HttpResponse<String> response = sendRequest("DELETE", endpoint(PRODUCT_PATH, id), null);
        if (isSuccess(response.statusCode())) {
            System.out.println("Produto removido.");
            return;
        }
        printApiError(response);
    }

    private void listComandas() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("GET", baseUrl + COMANDA_PATH + "?page=0&size=10&sort=id,desc", null);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        JsonNode root = mapper.readTree(response.body());
        List<ComandaResponse> comandas = mapper.convertValue(
                root.path("content"),
                mapper.getTypeFactory().constructCollectionType(List.class, ComandaResponse.class));
        System.out.println("\nComandas:");
        if (comandas == null || comandas.isEmpty()) {
            System.out.println("Nenhuma comanda cadastrada.");
            return;
        }
        for (ComandaResponse comanda : comandas) {
            printComanda(comanda);
            System.out.println("----");
        }
    }

    private void getComandaById() throws IOException, InterruptedException {
        Long id = readLong("ID da comanda: ");
        HttpResponse<String> response = sendRequest("GET", endpoint(COMANDA_PATH, id), null);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ComandaResponse responseDto = mapper.readValue(response.body(), ComandaResponse.class);
        printComanda(responseDto);
        if (responseDto.items != null && !responseDto.items.isEmpty()) {
            System.out.println("Itens:");
            responseDto.items.forEach(i ->
                    System.out.println("  #" + i.id + " | " + i.productName + " | qtd: " + i.quantity + " | unit: " + i.unitPrice + " | total: " + i.lineTotal));
        }
    }

    private void createComanda() throws IOException, InterruptedException {
        String customer = readLine("Nome do cliente: ");
        HttpResponse<String> response = sendRequest("POST", baseUrl + COMANDA_PATH, new CreateComandaRequest(customer));
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ComandaResponse created = mapper.readValue(response.body(), ComandaResponse.class);
        System.out.println("Comanda criada: #" + created.id + " (" + created.customerName + ")");
    }

    private void addItemToComanda() throws IOException, InterruptedException {
        Long comandaId = readLong("ID da comanda: ");
        Long productId = readLong("ID do produto: ");
        Integer quantity = readInt("Quantidade: ");
        HttpResponse<String> response = sendRequest(
                "POST",
                endpoint(COMANDA_PATH, comandaId) + "/items",
                new AddComandaItemRequest(productId, quantity)
        );
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ComandaResponse updated = mapper.readValue(response.body(), ComandaResponse.class);
        System.out.println("Item adicionado. Total atual: " + updated.total);
    }

    private void removeItemFromComanda() throws IOException, InterruptedException {
        Long comandaId = readLong("ID da comanda: ");
        Long itemId = readLong("ID do item da comanda: ");
        HttpResponse<String> response = sendRequest("DELETE", endpoint(COMANDA_PATH, comandaId) + "/items/" + itemId, null);
        if (isSuccess(response.statusCode())) {
            System.out.println("Item removido.");
        } else {
            printApiError(response);
        }
    }

    private void closeComanda() throws IOException, InterruptedException {
        Long id = readLong("ID da comanda: ");
        HttpResponse<String> response = sendRequest("PATCH", endpoint(COMANDA_PATH, id) + "/close", null);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ComandaResponse closed = mapper.readValue(response.body(), ComandaResponse.class);
        System.out.println("Comanda fechada com sucesso. Total: " + closed.total);
    }

    private void deleteComanda() throws IOException, InterruptedException {
        Long id = readLong("ID da comanda: ");
        HttpResponse<String> response = sendRequest("DELETE", endpoint(COMANDA_PATH, id), null);
        if (isSuccess(response.statusCode())) {
            System.out.println("Comanda removida.");
            return;
        }
        printApiError(response);
    }

    private ProductDto readProductData() {
        String name = readLine("Nome: ");
        String description = readLine("Descrição: ");
        Double price = readDouble("Preço: ");
        String category = readLine("Categoria: ");
        Integer stock = readInt("Estoque: ");
        boolean active = readBoolean("Ativo? (s/n): ");
        ProductDto dto = new ProductDto();
        dto.name = name;
        dto.description = description;
        dto.price = price;
        dto.category = category;
        dto.stockQuantity = stock;
        dto.active = active;
        return dto;
    }

    private HttpResponse<String> sendRequest(String method, String url, Object body) throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(8))
                .header("Content-Type", "application/json");

        switch (method) {
            case "GET" -> request.GET();
            case "DELETE" -> request.DELETE();
            case "POST", "PUT", "PATCH" -> {
                String payload = body == null ? "{}" : mapper.writeValueAsString(body);
                request.method(method, HttpRequest.BodyPublishers.ofString(payload));
            }
            default -> throw new IllegalArgumentException("Método inválido: " + method);
        }

        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String endpoint(String path, Long... ids) {
        String endpoint = path;
        if (ids.length > 0) {
            endpoint += "/" + ids[0];
        }
        if (ids.length > 1) {
            endpoint += "/" + ids[1];
        }
        return baseUrl + endpoint;
    }

    private boolean isSuccess(int status) {
        return status >= 200 && status < 300;
    }

    private void printProduct(ProductDto product) {
        System.out.printf("#%d %s | R$ %.2f | estoque: %d | ativo: %s | %s%n",
                product.id, product.name, product.price, product.stockQuantity, product.active ? "sim" : "não", product.description);
        if (product.category != null && !product.category.isBlank()) {
            System.out.println("  Categoria: " + product.category);
        }
    }

    private void printComanda(ComandaResponse comanda) {
        System.out.println("Comanda #" + comanda.id);
        System.out.println("Cliente: " + comanda.customerName);
        System.out.println("Status: " + comanda.status);
        System.out.println("Criada: " + comanda.createdAt);
        if (comanda.closedAt != null) {
            System.out.println("Fechada: " + comanda.closedAt);
        }
        System.out.println("Total: " + comanda.total);
    }

    private void printApiError(HttpResponse<String> response) {
        try {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            System.out.println("Falha " + error.status + " [" + error.code + "]: " + error.message);
            if (error.details != null && !error.details.isEmpty()) {
                System.out.println("Detalhes: " + error.details);
            }
        } catch (IOException e) {
            System.out.println("Falha " + response.statusCode() + ": " + response.body());
        }
    }

    private String readLine(String label) {
        System.out.print(label);
        return scanner.nextLine();
    }

    private Long readLong(String label) {
        while (true) {
            try {
                return Long.parseLong(readLine(label).trim());
            } catch (NumberFormatException ex) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private Integer readInt(String label) {
        while (true) {
            try {
                return Integer.parseInt(readLine(label).trim());
            } catch (NumberFormatException ex) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private Double readDouble(String label) {
        while (true) {
            try {
                String text = readLine(label).trim().replace(",", ".");
                return Double.parseDouble(text);
            } catch (NumberFormatException ex) {
                System.out.println("Digite um valor numérico válido.");
            }
        }
    }

    private boolean readBoolean(String label) {
        while (true) {
            String value = readLine(label).trim().toLowerCase();
            if ("s".equals(value) || "sim".equals(value) || "y".equals(value) || "yes".equals(value)) {
                return true;
            }
            if ("n".equals(value) || "nao".equals(value) || "não".equals(value) || "no".equals(value)) {
                return false;
            }
            System.out.println("Digite 's' ou 'n'.");
        }
    }

    private static class ProductDto {
        public Long id;
        public String name;
        public String description;
        public Double price;
        public String category;
        public Integer stockQuantity;
        public boolean active;
    }

    private static class StockRequest {
        public Integer delta;

        public StockRequest(Integer delta) {
            this.delta = delta;
        }
    }

    private static class CreateComandaRequest {
        public String customerName;

        public CreateComandaRequest(String customerName) {
            this.customerName = customerName;
        }
    }

    private static class AddComandaItemRequest {
        public Long productId;
        public Integer quantity;

        public AddComandaItemRequest(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }

    private static class ComandaResponse {
        public Long id;
        public String customerName;
        public String status;
        public String createdAt;
        public String closedAt;
        public Double total;
        public List<ComandaItemResponse> items;
    }

    private static class ComandaItemResponse {
        public Long id;
        public Long productId;
        public String productName;
        public Integer quantity;
        public Double unitPrice;
        public Double lineTotal;
    }

    private static class ApiError {
        public int status;
        public String code;
        public String message;
        public String path;
        public java.util.Map<String, Object> details;
    }
}
