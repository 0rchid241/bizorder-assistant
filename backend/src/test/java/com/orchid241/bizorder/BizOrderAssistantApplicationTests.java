package com.orchid241.bizorder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BizOrderAssistantApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    void healthReturnsUpWithoutAuthentication() throws Exception {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/health"))
                    .GET().build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.headers().firstValue("Content-Type").orElseThrow())
                    .startsWith("application/json");
            assertThat(objectMapper.readTree(response.body()).path("status").asString()).isEqualTo("UP");
        }
    }

    @Test
    void otherPathsAreForbidden() throws Exception {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/other"))
                    .GET().build();
            var response = client.send(request, HttpResponse.BodyHandlers.discarding());

            assertThat(response.statusCode()).isEqualTo(403);
        }
    }
}
