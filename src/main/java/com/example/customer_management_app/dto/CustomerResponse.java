package com.example.customer_management_app.dto;

// Creo este DTO para responder al frontend con un contrato estable,
// evitando exponer detalles internos de mi entidad (como setters de timestamps).

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CustomerResponse", description = "DTO de salida para representar clientes")
public class CustomerResponse {

    @Schema(description = "Unique identifier of the customer", example = "1")
    private Long id;

    @Schema(description = "Customer first name", example = "John")
    private String firstName;

    @Schema(description = "Customer last name", example = "Doe")
    private String lastName;

    @Schema(description = "Unique email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Phone number in the format XXX-XXXX (optional)", example = "123-4567")
    private String phone;

    @Schema(description = "Street address (optional)", example = "123 Main St, Springfield")
    private String address;

    @Schema(description = "Creation timestamp", example = "2025-08-10T12:34:56")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2025-08-10T12:34:56")
    private LocalDateTime updatedAt;

    public CustomerResponse() {}

    public CustomerResponse(Long id, String firstName, String lastName, String email,
                            String phone, String address, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters y setters (solo lectura lógica desde el frontend; backend puede construirlo libremente)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
