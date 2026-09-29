package com.orchid241.bizorder.product;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductApiTests {
    @LocalServerPort
    private int port;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private JdbcTemplate jdbc;
    private HttpClient client;
    private String prefix;

    @BeforeEach
    void setUp() {
        client = HttpClient.newHttpClient();
        prefix = "TEST" + UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ROOT);
    }

    @AfterEach
    void cleanUp() {
        // HTTP 서버의 트랜잭션은 테스트 스레드와 별개다. 이 테스트의 고유 코드만 삭제한다.
        try {
            jdbc.update("DELETE FROM products WHERE code LIKE ?", prefix + "%");
        } finally {
            client.close();
        }
    }

    @Test
    void createReadUpdateDeactivateAndReloadFromPostgres() throws Exception {
        var created = send("POST", "/api/products", mapper.writeValueAsString(new CreateProductRequest(
                " " + prefix.toLowerCase(Locale.ROOT) + " ", " 가상 인터넷 ", ProductCategory.INTERNET, "최초 설명")));
        assertThat(created.statusCode()).isEqualTo(201);
        JsonNode body = mapper.readTree(created.body());
        long id = body.path("id").asLong();
        assertThat(body.path("code").asString()).isEqualTo(prefix);
        assertThat(body.path("name").asString()).isEqualTo("가상 인터넷");
        assertThat(body.path("active").asBoolean()).isTrue();
        assertThat(created.headers().firstValue("Location")).contains("/api/products/" + id);

        var fetched = send("GET", "/api/products/" + id, null);
        assertThat(fetched.statusCode()).isEqualTo(200);
        assertThat(mapper.readTree(fetched.body())).isEqualTo(body);
        var listed = send("GET", "/api/products", null);
        assertThat(listed.statusCode()).isEqualTo(200);
        List<Long> ids = new ArrayList<>();
        mapper.readTree(listed.body()).forEach(node -> ids.add(node.path("id").asLong()));
        assertThat(ids).contains(id).isSorted();

        var updated = send("PUT", "/api/products/" + id, mapper.writeValueAsString(
                new UpdateProductRequest("수정 상품", ProductCategory.WIFI, "수정 설명", false)));
        assertThat(updated.statusCode()).isEqualTo(200);
        var reloaded = mapper.readTree(send("GET", "/api/products/" + id, null).body());
        assertThat(reloaded.path("name").asString()).isEqualTo("수정 상품");
        assertThat(reloaded.path("description").asString()).isEqualTo("수정 설명");
        assertThat(reloaded.path("category").asString()).isEqualTo("WIFI");
        assertThat(reloaded.path("active").asBoolean()).isFalse();
        assertThat(reloaded.path("code").asString()).isEqualTo(prefix);
        assertThat(jdbc.queryForObject("SELECT active FROM products WHERE id = ?", Boolean.class, id)).isFalse();
        assertThat(jdbc.queryForObject("SELECT name FROM products WHERE id = ?", String.class, id)).isEqualTo("수정 상품");

        var cleared = send("PUT", "/api/products/" + id,
                "{\"name\":\"상품\",\"category\":\"WIFI\",\"active\":true}");
        assertThat(cleared.statusCode()).isEqualTo(200);
        assertThat(jdbc.queryForObject("SELECT description FROM products WHERE id = ?", String.class, id)).isNull();
    }

    @Test
    void caseAndWhitespaceVariantsAreDuplicates() throws Exception {
        create();
        assertError(send("POST", "/api/products", "{\"code\":\" " + prefix.toLowerCase(Locale.ROOT)
                + " \",\"name\":\"상품\",\"category\":\"ETC\"}"), 409, "DUPLICATE_PRODUCT_CODE");
    }

    @Test
    void concurrentDuplicateRequestsHaveOneWinner() throws Exception {
        String body = mapper.writeValueAsString(new CreateProductRequest(prefix, "가상 상품", ProductCategory.ETC, null));
        var request = request("POST", "/api/products", body);
        var first = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        var second = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        CompletableFuture.allOf(first, second).join();
        assertThat(List.of(first.join().statusCode(), second.join().statusCode())).containsExactlyInAnyOrder(201, 409);
        var rejected = first.join().statusCode() == 409 ? first.join() : second.join();
        assertError(rejected, 409, "DUPLICATE_PRODUCT_CODE");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM products WHERE code = ?", Integer.class, prefix)).isEqualTo(1);
    }

    static Stream<String> invalidCreates() {
        return Stream.of(
                "{}",
                "{\"code\":\" \",\"name\":\"상품\",\"category\":\"ETC\"}",
                "{\"code\":\"VALID\",\"name\":\" \",\"category\":\"ETC\"}",
                "{\"code\":\"VALID\",\"name\":\"상품\",\"category\":\"UNKNOWN\"}",
                "{\"code\":\"VALID\",\"name\":\"상품\",\"category\":null}",
                "{\"code\":\"BAD-CODE\",\"name\":\"상품\",\"category\":\"ETC\"}",
                "{\"code\":\"" + "A".repeat(65) + "\",\"name\":\"상품\",\"category\":\"ETC\"}",
                "{\"code\":\"VALID\",\"name\":\"" + "가".repeat(101) + "\",\"category\":\"ETC\"}",
                "{\"code\":\"VALID\",\"name\":\"상품\",\"category\":\"ETC\",\"description\":\"" + "x".repeat(2001) + "\"}",
                "{\"code\":\"VALID\",\"name\":\"상품\",\"category\":\"ETC\",\"active\":false}",
                "{broken}");
    }

    @ParameterizedTest
    @MethodSource("invalidCreates")
    void rejectsInvalidCreate(String body) throws Exception {
        assertError(send("POST", "/api/products", body), 400, "INVALID_REQUEST");
    }

    @Test
    void acceptsMaximumLengths() throws Exception {
        String code = prefix + "A".repeat(64 - prefix.length());
        var response = send("POST", "/api/products", mapper.writeValueAsString(new CreateProductRequest(
                code, "가".repeat(100), ProductCategory.SECURITY, "x".repeat(2000))));
        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    void missingProductReturns404ForReadAndUpdate() throws Exception {
        assertError(send("GET", "/api/products/0", null), 404, "PRODUCT_NOT_FOUND");
        assertError(send("PUT", "/api/products/0", "{\"name\":\"상품\",\"category\":\"ETC\",\"active\":true}"),
                404, "PRODUCT_NOT_FOUND");
        assertError(send("GET", "/api/products/not-a-number", null), 400, "INVALID_REQUEST");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\" \",\"category\":\"ETC\",\"active\":true}",
            "{\"name\":\"상품\",\"category\":\"UNKNOWN\",\"active\":true}",
            "{\"name\":\"상품\",\"category\":\"ETC\"}",
            "{\"name\":\"상품\",\"category\":\"ETC\",\"active\":null}",
            "{\"name\":\"상품\",\"category\":\"ETC\",\"active\":true,\"code\":\"CHANGED\"}",
            "{\"name\":\"상품\",\"category\":\"ETC\",\"active\":true,\"code\":null}"
    })
    void rejectsInvalidUpdateWithoutChangingPersistedProduct(String body) throws Exception {
        long id = create();
        assertError(send("PUT", "/api/products/" + id, body), 400, "INVALID_REQUEST");
        assertThat(jdbc.queryForObject("SELECT code FROM products WHERE id = ?", String.class, id)).isEqualTo(prefix);
        assertThat(jdbc.queryForObject("SELECT name FROM products WHERE id = ?", String.class, id)).isEqualTo("가상 상품");
    }

    @Test
    void databaseEnforcesUniqueAndRequiredFields() throws Exception {
        create();
        assertThatThrownBy(() -> jdbc.update("INSERT INTO products (code, name, category) VALUES (?, '상품', 'ETC')", prefix))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("INSERT INTO products (code, name, category) VALUES (?, NULL, 'ETC')", prefix + "N"))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("INSERT INTO products (code, name, category) VALUES (?, '상품', 'UNKNOWN')", prefix + "C"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deleteAndUnrelatedWritesRemainForbidden() throws Exception {
        long id = create();
        assertThat(send("DELETE", "/api/products/" + id, null).statusCode()).isEqualTo(403);
        assertThat(send("POST", "/api/other", "{}").statusCode()).isEqualTo(403);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM products WHERE id = ?", Integer.class, id)).isEqualTo(1);
    }

    @Test
    void openApiDescribesProductRequestsAndResponses() throws Exception {
        var response = send("GET", "/v3/api-docs", null);
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode document = mapper.readTree(response.body());
        assertThat(document.path("paths").path("/api/products").has("get")).isTrue();
        assertThat(document.path("paths").path("/api/products").path("post").path("responses").has("201")).isTrue();
        assertThat(document.path("paths").path("/api/products/{id}").has("put")).isTrue();
        assertThat(document.path("components").path("schemas").path("CreateProductRequest").path("properties").has("code")).isTrue();
        assertThat(document.path("components").path("schemas").path("ProductResponse").path("properties").has("active")).isTrue();
        assertThat(send("GET", "/swagger-ui/index.html", null).statusCode()).isEqualTo(200);
    }

    private long create() throws Exception {
        var response = send("POST", "/api/products", mapper.writeValueAsString(
                new CreateProductRequest(prefix, "가상 상품", ProductCategory.ETC, null)));
        assertThat(response.statusCode()).isEqualTo(201);
        return mapper.readTree(response.body()).path("id").asLong();
    }

    private HttpRequest request(String method, String path, String body) {
        return HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private HttpResponse<String> send(String method, String path, String body) throws Exception {
        return client.send(request(method, path, body), HttpResponse.BodyHandlers.ofString());
    }

    private void assertError(HttpResponse<String> response, int status, String code) {
        assertThat(response.statusCode()).isEqualTo(status);
        assertThat(mapper.readTree(response.body()).path("code").asString()).isEqualTo(code);
        assertThat(mapper.readTree(response.body()).path("message").asString()).isNotBlank();
    }
}
