package com.example.customer_management_app;

import com.example.customer_management_app.dto.CustomerRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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

    @BeforeEach
    void cleanDatabase() {
        // Limpio la base antes de cada test para datos repetibles
        customerRepository.deleteAll();
    }

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
