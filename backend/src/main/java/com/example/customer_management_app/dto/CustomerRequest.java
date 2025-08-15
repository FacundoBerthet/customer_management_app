package com.example.customer_management_app.dto;

// Creo este DTO para recibir datos desde el frontend sin exponer mi entidad JPA directamente
// y concentrar aquí las validaciones de entrada.

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CustomerRequest", description = "DTO de entrada para crear/actualizar clientes")
public class CustomerRequest {

    // Nombre y apellido: mantengo las mismas reglas que en la entidad, pero aquí se validan
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 40, message = "First name must be between 2 and 40 characters")
    @Schema(description = "Customer first name", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 40, message = "Last name must be between 2 and 40 characters")
    @Schema(description = "Customer last name", example = "Doe")
    private String lastName;

    // Email: requerido y con formato válido; la unicidad la controlo en el servicio/repositorio
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid (example@example.com)")
    @Schema(description = "Unique email address", example = "john.doe@example.com")
    private String email;

    // Teléfono opcional con el mismo patrón sencillo que definí en la entidad
    @Pattern(regexp = "^$|^\\d{3}-\\d{4}$",
             message = "The phone must be in the format XXX-XXXX or empty")
    @Schema(description = "Phone number in the format XXX-XXXX (optional)", example = "123-4567", pattern = "^$|^\\d{3}-\\d{4}$")
    private String phone;

    // Dirección opcional con límite de longitud
    @Size(max = 100, message = "Address must be less than 100 characters")
    @Schema(description = "Street address (optional)", example = "123 Main St, Springfield")
    private String address;

    // Constructor vacío requerido por Spring para el binding de @RequestBody
    public CustomerRequest() {}

    // Constructor de conveniencia por si quiero instanciarlo en tests
    public CustomerRequest(String firstName, String lastName, String email, String phone, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    // Getters y setters
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
}
