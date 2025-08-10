package com.example.customer_management_app;

// En este archivo de pruebas quiero verificar que mi mapper haga exactamente
// las transformaciones que espero entre la entidad JPA y mis DTOs.
// Así me aseguro de no exponer la entidad y de mantener un contrato estable hacia el frontend.

import com.example.customer_management_app.dto.CustomerRequest;
import com.example.customer_management_app.dto.CustomerResponse;
import com.example.customer_management_app.mapper.CustomerMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Declaro esta clase de test para cubrir los métodos del CustomerMapper.
// Mi objetivo es validar (campo por campo) los caminos principales: toResponse, fromRequest y updateEntityFromRequest.
class CustomerMapperTest {

    @Test
    // En esta prueba quiero chequear que toResponse copie todos los campos desde la entidad hacia el DTO de salida.
    // Sigo el patrón Arrange-Act-Assert para que sea claro qué preparo, qué ejecuto y qué verifico.
    void toResponse_mapsAllFields() {
        // Arrange: construyo una entidad completa con timestamps controlados para que las aserciones sean determinísticas
        Customer entity = new Customer(1L, "John", "Doe", "john.doe@example.com", "123-4567", "123 Main St");
        LocalDateTime created = LocalDateTime.of(2025, 8, 10, 12, 0, 0);
        LocalDateTime updated = LocalDateTime.of(2025, 8, 10, 12, 30, 0);
        entity.setCreatedAt(created);
        entity.setUpdatedAt(updated);

        // Act: convierto la entidad al DTO de respuesta
        CustomerResponse dto = CustomerMapper.toResponse(entity);

        // Assert: verifico campo a campo que el mapeo sea correcto
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("john.doe@example.com", dto.getEmail());
        assertEquals("123-4567", dto.getPhone());
        assertEquals("123 Main St", dto.getAddress());
        assertEquals(created, dto.getCreatedAt());
        assertEquals(updated, dto.getUpdatedAt());
    }

    @Test
    // En esta prueba quiero asegurarme de que fromRequest construya una entidad nueva con los valores del DTO de entrada.
    // También verifico que el id quede null porque lo asigna la base (o el servicio), no el cliente.
    void fromRequest_buildsEntity() {
        // Arrange: armo un DTO de entrada representativo
        CustomerRequest req = new CustomerRequest("Jane", "Roe", "jane.roe@example.com", "987-6543", "742 Evergreen");

        // Act: transformo el request en entidad
        Customer entity = CustomerMapper.fromRequest(req);

        // Assert: valido que se hayan mapeado todos los campos correctamente
        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals("Jane", entity.getFirstName());
        assertEquals("Roe", entity.getLastName());
        assertEquals("jane.roe@example.com", entity.getEmail());
        assertEquals("987-6543", entity.getPhone());
        assertEquals("742 Evergreen", entity.getAddress());
    }

    @Test
    // En esta prueba quiero confirmar que updateEntityFromRequest actualice cada campo mutable de la entidad existente.
    // Mantengo el id intacto y me apoyo en los setters de la entidad para timestamps.
    void updateEntityFromRequest_updatesFields() {
        // Arrange: preparo una entidad existente y un request con nuevos datos
        Customer entity = new Customer(1L, "Old", "Name", "old@example.com", "111-1111", "Old St");
        CustomerRequest req = new CustomerRequest("New", "Name", "new@example.com", "222-2222", "New St");

        // Act: aplico la actualización de campos
        CustomerMapper.updateEntityFromRequest(entity, req);

        // Assert: verifico que los campos hayan cambiado según el request
        assertEquals("New", entity.getFirstName());
        assertEquals("Name", entity.getLastName());
        assertEquals("new@example.com", entity.getEmail());
        assertEquals("222-2222", entity.getPhone());
        assertEquals("New St", entity.getAddress());
    }
}
