package com.Captando.demo.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MarketConsoleApp {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static final String PRODUCT_PATH = "/products";
    private static final String CLIENT_PATH = "/clients";
    private static final String COMANDA_PATH = "/comandas";
    private static final String CART_PATH = "/carts";

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
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
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
                    case "7" -> listClients();
                    case "8" -> getClientById();
                    case "9" -> createClient();
                    case "10" -> updateClient();
                    case "11" -> deleteClient();
                    case "12" -> listComandas();
                    case "13" -> getComandaById();
                    case "14" -> createComanda();
                    case "15" -> addItemToComanda();
                    case "16" -> removeItemFromComanda();
                    case "17" -> applyComandaDiscount();
                    case "18" -> setComandaPayment();
                    case "19" -> checkoutComanda();
                    case "20" -> closeComanda();
                    case "21" -> deleteComanda();
                    case "22" -> listCarts();
                    case "23" -> createCart();
                    case "24" -> addItemToCart();
                    case "25" -> removeItemFromCart();
                    case "26" -> checkoutCart();
                    case "27" -> showPaymentMethods();
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
        System.out.println("""

==== Mercado Captando (CLI) ====
1) Produtos: listar
2) Produtos: buscar por id
3) Produtos: criar
4) Produtos: atualizar
5) Produtos: ajustar estoque
6) Produtos: remover
7) Clientes: listar
8) Clientes: buscar por id
9) Clientes: criar
10) Clientes: atualizar
11) Clientes: remover
12) Comandas: listar
13) Comandas: buscar por id
14) Comandas: criar
15) Comandas: adicionar item
16) Comandas: remover item
17) Comandas: aplicar desconto
18) Comandas: definir forma de pagamento
19) Comandas: checkout
20) Comandas: fechar (sem método)
21) Comandas: remover
22) Carrinho: listar
23) Carrinho: criar
24) Carrinho: adicionar item
25) Carrinho: remover item
26) Carrinho: checkout
27) Formas de pagamento aceitas
0) Sair
        """);
    }

    private void listProducts() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("GET", baseUrl + PRODUCT_PATH + "?page=0&size=10&sort=id,asc", null);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        JsonNode root = mapper.readTree(response.body());
        List<ProductDto> products = mapper.convertValue(
                root.path("content"),
                mapper.getTypeFactory().constructCollectionType(List.class, ProductDto.class)
        );
        System.out.println("Produtos:");
        if (products == null || products.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        for (ProductDto product : products) {
            printProduct(product);
        }
        System.out.printf("Total: %d | Página: %d de %d%n",
                root.path("totalElements").asLong(0),
                root.path("number").asInt(0) + 1,
                root.path("totalPages").asInt(0));
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
        System.out.println("Produto criado:");
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
        System.out.println("Produto atualizado:");
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

    private void listClients() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("GET", baseUrl + CLIENT_PATH + "?page=0&size=10&sort=id,asc", null);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        JsonNode root = mapper.readTree(response.body());
        List<ClientDto> customers = mapper.convertValue(
                root.path("content"),
                mapper.getTypeFactory().constructCollectionType(List.class, ClientDto.class)
        );
        if (customers == null || customers.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        System.out.println("Clientes:");
        for (ClientDto customer : customers) {
            printClient(customer);
        }
        System.out.printf("Total: %d | Página: %d de %d%n",
                root.path("totalElements").asLong(0),
                root.path("number").asInt(0) + 1,
                root.path("totalPages").asInt(0));
    }

    private void getClientById() throws IOException, InterruptedException {
        Long id = readLong("ID do cliente: ");
        HttpResponse<String> response = sendRequest("GET", endpoint(CLIENT_PATH, id), null);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ClientDto customer = mapper.readValue(response.body(), ClientDto.class);
        printClient(customer);
    }

    private void createClient() throws IOException, InterruptedException {
        ClientDto request = readClientData();
        HttpResponse<String> response = sendRequest("POST", baseUrl + CLIENT_PATH, request);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ClientDto created = mapper.readValue(response.body(), ClientDto.class);
        System.out.println("Cliente criado:");
        printClient(created);
    }

    private void updateClient() throws IOException, InterruptedException {
        Long id = readLong("ID do cliente: ");
        ClientDto request = readClientData();
        HttpResponse<String> response = sendRequest("PUT", endpoint(CLIENT_PATH, id), request);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ClientDto updated = mapper.readValue(response.body(), ClientDto.class);
        System.out.println("Cliente atualizado:");
        printClient(updated);
    }

    private void deleteClient() throws IOException, InterruptedException {
        Long id = readLong("ID do cliente: ");
        HttpResponse<String> response = sendRequest("DELETE", endpoint(CLIENT_PATH, id), null);
        if (isSuccess(response.statusCode())) {
            System.out.println("Cliente removido.");
            return;
        }
        printApiError(response);
    }

    private void listComandas() throws IOException, InterruptedException {
        listComandasByPath(COMANDA_PATH, "Comandas");
    }

    private void listCarts() throws IOException, InterruptedException {
        listComandasByPath(CART_PATH, "Carrinhos");
    }

    private void listComandasByPath(String path, String label) throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("GET", baseUrl + path + "?page=0&size=10&sort=id,desc", null);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        JsonNode root = mapper.readTree(response.body());
        List<ComandaResponse> list = mapper.convertValue(
                root.path("content"),
                mapper.getTypeFactory().constructCollectionType(List.class, ComandaResponse.class)
        );
        System.out.println(label + ":");
        if (list == null || list.isEmpty()) {
            System.out.println("Nenhum registro.");
            return;
        }
        for (ComandaResponse comanda : list) {
            printComanda(comanda);
            System.out.println("----");
        }
    }

    private void getComandaById() throws IOException, InterruptedException {
        Long id = readLong("ID da comanda/carrinho: ");
        ComandaResponse responseDto = readComandaByPath(COMANDA_PATH, id);
        printComanda(responseDto);
    }

    private void createComanda() throws IOException, InterruptedException {
        String customerName = readLine("Nome do cliente: ");
        Long customerId = readOptionalLong("Cliente existente (id, opcional): ");
        CreateComandaRequest request = new CreateComandaRequest(customerName, customerId);
        HttpResponse<String> response = sendRequest("POST", baseUrl + COMANDA_PATH, request);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ComandaResponse created = mapper.readValue(response.body(), ComandaResponse.class);
        System.out.println("Comanda criada: #" + created.id + " | Cliente: " + created.customerName);
    }

    private void createCart() throws IOException, InterruptedException {
        String customerName = readLine("Nome do cliente: ");
        Long customerId = readOptionalLong("Cliente existente (id, opcional): ");
        CreateComandaRequest request = new CreateComandaRequest(customerName, customerId);
        HttpResponse<String> response = sendRequest("POST", baseUrl + CART_PATH, request);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ComandaResponse created = mapper.readValue(response.body(), ComandaResponse.class);
        System.out.println("Carrinho criado: #" + created.id + " | Cliente: " + created.customerName);
    }

    private void addItemToComanda() throws IOException, InterruptedException {
        Long comandaId = readLong("ID da comanda: ");
        AddComandaItemRequest request = readComandaItemRequest();
        HttpResponse<String> response = sendRequest(
                "POST",
                endpoint(COMANDA_PATH, comandaId) + "/items",
                request
        );
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ComandaResponse updated = mapper.readValue(response.body(), ComandaResponse.class);
        System.out.println("Item adicionado. Total atual: " + updated.total);
    }

    private void addItemToCart() throws IOException, InterruptedException {
        Long cartId = readLong("ID do carrinho: ");
        AddComandaItemRequest request = readComandaItemRequest();
        HttpResponse<String> response = sendRequest(
                "POST",
                endpoint(CART_PATH, cartId) + "/items",
                request
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
        HttpResponse<String> response = sendRequest(
                "DELETE",
                endpoint(COMANDA_PATH, comandaId) + "/items/" + itemId,
                null
        );
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ComandaResponse updated = mapper.readValue(response.body(), ComandaResponse.class);
        System.out.println("Item removido. Total atual: " + updated.total);
    }

    private void removeItemFromCart() throws IOException, InterruptedException {
        Long cartId = readLong("ID do carrinho: ");
        Long itemId = readLong("ID do item do carrinho: ");
        HttpResponse<String> response = sendRequest(
                "DELETE",
                endpoint(CART_PATH, cartId) + "/items/" + itemId,
                null
        );
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ComandaResponse updated = mapper.readValue(response.body(), ComandaResponse.class);
        System.out.println("Item removido. Total atual: " + updated.total);
    }

    private void applyComandaDiscount() throws IOException, InterruptedException {
        Long id = readLong("ID da comanda: ");
        ApplyDiscountRequest request = new ApplyDiscountRequest(readDouble("Desconto percentual: "), readDouble("Desconto fixo: "));
        HttpResponse<String> response = sendRequest("PATCH", endpoint(COMANDA_PATH, id) + "/discount", request);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ComandaResponse responseDto = mapper.readValue(response.body(), ComandaResponse.class);
        System.out.println("Desconto aplicado. Novo total: " + responseDto.total);
    }

    private void setComandaPayment() throws IOException, InterruptedException {
        Long id = readLong("ID da comanda: ");
        String raw = readLine("Método de pagamento (ex.: CASH, PIX, DEBIT_CARD, CREDIT_CARD, FOOD_VOUCHER, TRANSFER): ").trim();
        HttpResponse<String> response = sendRequest("PATCH", endpoint(COMANDA_PATH, id) + "/payment?paymentMethod=" + raw, null);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ComandaResponse responseDto = mapper.readValue(response.body(), ComandaResponse.class);
        System.out.println("Método definido: " + responseDto.paymentMethod);
    }

    private void checkoutComanda() throws IOException, InterruptedException {
        Long id = readLong("ID da comanda: ");
        String raw = readLine("Método de pagamento do checkout: ").trim();
        HttpResponse<String> response = sendRequest("PATCH", endpoint(COMANDA_PATH, id) + "/checkout",
                new CheckoutRequest(raw));
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ComandaResponse responseDto = mapper.readValue(response.body(), ComandaResponse.class);
        System.out.println("Checkout finalizado com sucesso.");
        printComanda(responseDto);
    }

    private void checkoutCart() throws IOException, InterruptedException {
        Long id = readLong("ID do carrinho: ");
        String raw = readLine("Método de pagamento do checkout: ").trim();
        HttpResponse<String> response = sendRequest("PATCH", endpoint(CART_PATH, id) + "/checkout",
                new CheckoutRequest(raw));
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        ComandaResponse responseDto = mapper.readValue(response.body(), ComandaResponse.class);
        System.out.println("Checkout do carrinho finalizado.");
        printComanda(responseDto);
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

    private void showPaymentMethods() throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("GET", baseUrl + COMANDA_PATH + "/payment-methods", null);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return;
        }
        List<String> methods = mapper.readValue(response.body(),
                mapper.getTypeFactory().constructCollectionType(List.class, String.class));
        System.out.println("Formas de pagamento:");
        for (String method : methods) {
            System.out.println(" - " + method);
        }
    }

    private ComandaResponse readComandaByPath(String path, Long id) throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest("GET", endpoint(path, id), null);
        if (!isSuccess(response.statusCode())) {
            printApiError(response);
            return null;
        }
        return mapper.readValue(response.body(), ComandaResponse.class);
    }

    private ProductDto readProductData() {
        ProductDto dto = new ProductDto();
        dto.name = readLine("Nome: ");
        dto.description = readLine("Descrição: ");
        dto.price = readDouble("Preço: ");
        dto.category = readLine("Categoria: ");
        dto.stockQuantity = readInt("Estoque: ");
        dto.active = readBoolean("Ativo? (s/n): ");
        return dto;
    }

    private ClientDto readClientData() {
        ClientDto dto = new ClientDto();
        dto.name = readLine("Nome: ");
        dto.email = readLine("E-mail: ");
        dto.phone = readLine("Telefone: ");
        return dto;
    }

    private AddComandaItemRequest readComandaItemRequest() {
        Long productId = readLong("ID do produto: ");
        Integer quantity = readInt("Quantidade: ");
        return new AddComandaItemRequest(productId, quantity);
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

    private void printClient(ClientDto customer) {
        System.out.println("#" + customer.id + " " + customer.name + " | " + customer.email + " | " + customer.phone);
    }

    private void printComanda(ComandaResponse comanda) {
        if (comanda == null) {
            return;
        }
        System.out.println("Comanda #" + comanda.id);
        if (comanda.customerId != null) {
            System.out.println("Cliente: " + comanda.customerName + " (id " + comanda.customerId + ")");
        } else {
            System.out.println("Cliente: " + comanda.customerName);
        }
        System.out.println("Status: " + comanda.status);
        System.out.println("Criada: " + comanda.createdAt);
        if (comanda.closedAt != null) {
            System.out.println("Fechada: " + comanda.closedAt);
        }
        System.out.println("Subtotal: " + formatMoney(comanda.subtotal));
        System.out.println("Desconto (%): " + comanda.discountPercent + "  Valor: " + formatMoney(comanda.discountAmount));
        System.out.println("Método: " + comanda.paymentMethod);
        System.out.println("Total: " + formatMoney(comanda.total));

        if (comanda.items == null || comanda.items.isEmpty()) {
            System.out.println("Itens: nenhum");
            return;
        }
        System.out.println("Itens:");
        for (ComandaItemResponse item : comanda.items) {
            System.out.println("  #" + item.id + " | " + item.productName + " | qtd: " + item.quantity
                    + " | unit: " + formatMoney(item.unitPrice) + " | linha: " + formatMoney(item.lineTotal));
        }
    }

    private String formatMoney(Double value) {
        double safe = value == null ? 0.0 : value;
        return String.format("R$ %.2f", safe);
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

    private Long readOptionalLong(String label) {
        String raw = readLine(label).trim();
        if (raw.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            System.out.println("Valor inválido. Mantendo vazio.");
            return null;
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

    private static class ClientDto {
        public Long id;
        public String name;
        public String email;
        public String phone;
    }

    private static class CreateComandaRequest {
        public String customerName;
        public Long customerId;

        public CreateComandaRequest(String customerName, Long customerId) {
            this.customerName = customerName;
            this.customerId = customerId;
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

    private static class ApplyDiscountRequest {
        public Double discountPercent;
        public Double discountAmount;

        public ApplyDiscountRequest(Double discountPercent, Double discountAmount) {
            this.discountPercent = discountPercent;
            this.discountAmount = discountAmount;
        }
    }

    private static class CheckoutRequest {
        public String paymentMethod;

        public CheckoutRequest(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
    }

    private static class ComandaResponse {
        public Long id;
        public String customerName;
        public Long customerId;
        public String status;
        public String createdAt;
        public String closedAt;
        public Double subtotal;
        public Double discountPercent;
        public Double discountAmount;
        public Double total;
        public String paymentMethod;
        public List<ComandaItemResponse> items = new ArrayList<>();
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
