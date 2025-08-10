package com.example.customer_management_app.mapper;

// Creo este mapper manual para separar la lógica de transformación
// entre la entidad JPA y los DTOs. Mantengo todo simple y explícito.

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.example.customer_management_app.Customer;
import com.example.customer_management_app.dto.CustomerRequest;
import com.example.customer_management_app.dto.CustomerResponse;

public final class CustomerMapper {

    private CustomerMapper() { /* utility class */ }

    // Convierto una entidad Customer en un CustomerResponse para devolver al frontend
    public static CustomerResponse toResponse(Customer c) {
        if (c == null) return null;
        CustomerResponse r = new CustomerResponse();
        r.setId(c.getId());
        r.setFirstName(c.getFirstName());
        r.setLastName(c.getLastName());
        r.setEmail(c.getEmail());
        r.setPhone(c.getPhone());
        r.setAddress(c.getAddress());
        r.setCreatedAt(c.getCreatedAt());
        r.setUpdatedAt(c.getUpdatedAt());
        return r;
    }

    // Convierto una lista de entidades a una lista de DTOs de respuesta
    public static List<CustomerResponse> toResponseList(List<Customer> customers) {
        if (customers == null) return List.of();
        return customers.stream()
                .filter(Objects::nonNull)
                .map(CustomerMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Construyo una entidad nueva a partir de un CustomerRequest (para POST)
    public static Customer fromRequest(CustomerRequest req) {
        if (req == null) return null;
        return new Customer(
            req.getFirstName(),
            req.getLastName(),
            req.getEmail(),
            req.getPhone(),
            req.getAddress()
        );
    }

    // Actualizo una entidad existente con los datos del request (para PUT)
    public static void updateEntityFromRequest(Customer entity, CustomerRequest req) {
        if (entity == null || req == null) return;
        entity.setFirstName(req.getFirstName());
        entity.setLastName(req.getLastName());
        entity.setEmail(req.getEmail());
        entity.setPhone(req.getPhone());
        entity.setAddress(req.getAddress());
        // Los setters ya actualizan updatedAt en la entidad
    }
}
