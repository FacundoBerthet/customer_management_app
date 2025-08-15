package com.example.customer_management_app;

// En este test quiero validar de manera mínima que los endpoints paginados
// respondan con 200 y estructuren la respuesta esperada.

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional // Rollback por test
class PaginationControllerTest {

    @LocalServerPort
    int port;

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void getCustomersPaged_returns200() {
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> resp = rt.getForEntity(baseUrl("/api/customers/page?page=0&size=1"), String.class);
        assertEquals(200, resp.getStatusCode().value());
        assertTrue(resp.getBody().contains("content"));
        assertTrue(resp.getBody().contains("totalElements"));
    }

    @Test
    void searchCustomersPaged_returns200() {
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> resp = rt.getForEntity(baseUrl("/api/customers/search/page?q=a&page=0&size=1"), String.class);
        assertEquals(200, resp.getStatusCode().value());
        assertTrue(resp.getBody().contains("content"));
        assertTrue(resp.getBody().contains("totalPages"));
    }
}
