package com.example.customer_management_app;

import com.example.customer_management_app.dto.CustomerRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración del controlador usando MockMvc.
 * 
 * Objetivo:
 * - Verificar el contrato HTTP real (status, JSON, rutas).
 * - Cubrir casos básicos: creación, duplicados y paginación.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Cada test corre en transacción y hace rollback al final
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper; // Para serializar objetos a JSON

    @Test
    @DisplayName("POST /api/customers -> 201 Created")
    void createCustomer_shouldReturn201() throws Exception {
        // Datos mínimos válidos
        CustomerRequest req = new CustomerRequest();
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setEmail("john.doe@example.com");
        req.setPhone("123-4567");
        req.setAddress("123 Main St");

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            // Verifico algunos campos del DTO de respuesta
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("POST /api/customers (email duplicado) -> 409 Conflict")
    void createCustomer_duplicateEmail_shouldReturn409() throws Exception {
        // Preparo un cliente inicial (primer alta)
        Customer existing = new Customer("Jane", "Doe", "jane.doe@example.com", "123-4567", "742 Evergreen");
        customerRepository.save(existing);

        // Intento crear otro con el mismo email
        CustomerRequest req = new CustomerRequest();
        req.setFirstName("Another");
        req.setLastName("Person");
        req.setEmail("jane.doe@example.com"); // duplicado

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/customers (payload inválido) -> 400 Bad Request")
    void createCustomer_invalidEmail_shouldReturn400() throws Exception {
        CustomerRequest req = new CustomerRequest();
        req.setFirstName(""); // inválido (NotBlank)
        req.setLastName("Doe");
        req.setEmail("not-an-email"); // inválido (Email)

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/customers/by-email -> 200 OK cuando existe")
    void getByEmail_shouldReturn200() throws Exception {
        String uniqueEmail = "lookup_" + System.currentTimeMillis() + "@example.com";
        customerRepository.save(new Customer("Lu", "Khan", uniqueEmail, "123-4567", "Addr"));

        mockMvc.perform(get("/api/customers/by-email").param("email", uniqueEmail))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(uniqueEmail));
    }

    @Test
    @DisplayName("GET /api/customers/by-email -> 404 cuando NO existe")
    void getByEmail_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/customers/by-email").param("email", "no.such@example.com"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/customers/by-phone -> 200 OK cuando existe")
    void getByPhone_shouldReturn200() throws Exception {
        String phone = "555-0001";
        String email = "phone_" + System.currentTimeMillis() + "@example.com";
        customerRepository.save(new Customer("Pho", "Ne", email, phone, "Addr"));

        mockMvc.perform(get("/api/customers/by-phone").param("phone", phone))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.phone").value(phone));
    }

    @Test
    @DisplayName("GET /api/customers/by-phone -> 404 cuando NO existe")
    void getByPhone_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/customers/by-phone").param("phone", "999-9999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/customers/page con size muy grande -> se capa a 50")
    void pageSizeCapped_shouldUse50() throws Exception {
        // Inserto al menos 60 clientes con emails únicos
        IntStream.range(0, 60).forEach(i -> {
            String email = "bulk" + i + "_" + System.currentTimeMillis() + "@example.com";
            customerRepository.save(new Customer("Name" + i, "Last" + i, email, "123-" + String.format("%04d", i % 10000), "Street " + i));
        });

        mockMvc.perform(get("/api/customers/page")
                .param("page", "0")
                .param("size", "999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size").value(50))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(50));
    }

    @Test
    @DisplayName("GET /api/customers/page -> 200 OK con estructura de paginado")
    void getCustomersPaged_shouldReturnPageResponse() throws Exception {
        // Inserto algunos clientes para tener datos
        customerRepository.save(new Customer("Alice", "Smith", "alice@example.com", "123-4567", "Street 1"));
        customerRepository.save(new Customer("Bob", "Taylor", "bob@example.com", "234-5678", "Street 2"));
        customerRepository.save(new Customer("Carol", "Jones", "carol@example.com", "345-6789", "Street 3"));

        mockMvc.perform(get("/api/customers/page")
                .param("page", "0")
                .param("size", "2")
                .param("sort", "id,desc"))
            .andExpect(status().isOk())
            // Verifico campos típicos del wrapper PageResponse
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(2))
            .andExpect(jsonPath("$.totalElements").exists())
            .andExpect(jsonPath("$.totalPages").exists())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2));
    }
}
