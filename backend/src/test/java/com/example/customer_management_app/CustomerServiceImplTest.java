package com.example.customer_management_app;

import org.junit.jupiter.api.BeforeEach;// Importa las anotaciones de JUnit 5
import org.junit.jupiter.api.Test;// Importa las anotaciones de JUnit 5
// import org.mockito.Mockito; // Importa las anotaciones de Mockito
import org.mockito.InjectMocks; // Permite inyectar mocks en la clase de prueba
import org.mockito.Mock; // Permite crear un mock de CustomerRepository
import org.junit.jupiter.api.extension.ExtendWith; // Importa la anotación para extender con Mockito
import org.mockito.junit.jupiter.MockitoExtension; // Importa la extensión de Mockito para JUnit 5

import java.util.Arrays;
import java.util.List;
import java.util.Optional; // Importa las clases necesarias para pruebas unitarias

import static org.junit.jupiter.api.Assertions.*; // Importa las aserciones de JUnit 5
import static org.mockito.Mockito.*;

/*
 * Clase de prueba para CustomerServiceImpl.
 * Utiliza Mockito para simular el comportamiento del repositorio y JUnit 5 para las pruebas.
 */

@ExtendWith(MockitoExtension.class) // Extiende la clase de prueba con Mockito para usar sus funcionalidades
class CustomerServiceImplTest {

    @Mock // Crea un mock de CustomerRepository
    private CustomerRepository customerRepository;

    @InjectMocks // Inyecta el mock en CustomerServiceImpl
    // Esto permite que CustomerServiceImpl use el mock de CustomerRepository en lugar de una implementación
    private CustomerServiceImpl customerService;

    private Customer customer;
    private Customer customer2;

    @BeforeEach // Método que se ejecuta antes de cada prueba para inicializar los objetos necesarios
    void setUp() {
        customer = new Customer(1L, "Juan", "Perez", "juan.perez@gmail.com", "123456789", "Calle Falsa 123");
        customer2 = new Customer(2L, "Ana", "Gomez", "ana.gomez@ctu.gov", "987654321", "Av. Siempre Viva 742");
    }

    // Métodos de prueba para CustomerServiceImpl
    // Cada método prueba una funcionalidad específica del servicio de clientes

    @Test
    // Prueba para crear un cliente exitosamente
    void testCreateCustomerSuccess() { 
        when(customerRepository.existsByEmail(customer.getEmail())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        Customer created = customerService.createCustomer(customer);
        assertNotNull(created);
        assertEquals("Juan", created.getFirstName());
    }

    @Test
    // Prueba para crear un cliente con email duplicado
    void testCreateCustomerDuplicateEmail() {
        when(customerRepository.existsByEmail(customer.getEmail())).thenReturn(true);
        Exception ex = assertThrows(DuplicateEmailException.class, () -> customerService.createCustomer(customer));
        assertTrue(ex.getMessage().contains("Email already exists"));
    }

    @Test
    // Prueba para obtener un cliente por ID
    void testGetCustomerByIdFound() {
        lenient().when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        Optional<Customer> result = customerService.getCustomerById(1L);
        assertTrue(result.isPresent());
        assertEquals("Juan", result.get().getFirstName());
    }

    @Test
    // Prueba para obtener un cliente por ID que no existe
    void testGetCustomerByIdNotFound() {
        lenient().when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Customer> result = customerService.getCustomerById(99L);
        assertFalse(result.isPresent());
    }

    @Test
    // Prueba para actualizar un cliente exitosamente
    void testUpdateCustomerSuccess() {
        lenient().when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail(customer2.getEmail())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer2);
        Customer updated = customerService.updateCustomer(1L, customer2);
        assertEquals("Ana", updated.getFirstName());
        assertEquals("Gomez", updated.getLastName());
    }

    @Test
    // Prueba para actualizar un cliente con email duplicado
    void testUpdateCustomerEmailDuplicate() {
        lenient().when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail(customer2.getEmail())).thenReturn(true);
        Exception ex = assertThrows(DuplicateEmailException.class, () -> customerService.updateCustomer(1L, customer2));
        assertTrue(ex.getMessage().contains("Email already exists"));
    }

    @Test
    // Prueba para eliminar un cliente exitosamente
    void testDeleteCustomerSuccess() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);
        assertDoesNotThrow(() -> customerService.deleteCustomer(1L));
    }

    @Test
    // Prueba para eliminar un cliente que no existe
    void testDeleteCustomerNotFound() {
        when(customerRepository.existsById(99L)).thenReturn(false);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> customerService.deleteCustomer(99L));
        assertTrue(ex.getMessage().contains("Customer not found"));
    }

    @Test
    // Prueba para listar todos los clientes
    void testSearchCustomers() {
        when(customerRepository.findByFirstNameContainingOrLastNameContaining("Juan", "Juan"))
            .thenReturn(Arrays.asList(customer));
        List<Customer> result = customerService.searchCustomers("Juan");
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getFirstName());
    }

    @Test
    // Prueba para buscar clientes por nombre y apellido
    void testExistsByEmail() {
        when(customerRepository.existsByEmail("juan.perez@gmail.com")).thenReturn(true);
        assertTrue(customerService.existsByEmail("juan.perez@gmail.com"));
        when(customerRepository.existsByEmail("no.existe@gmail.com")).thenReturn(false);
        assertFalse(customerService.existsByEmail("no.existe@gmail.com"));
    }

    @Test
    // Prueba para contar clientes por apellido
    void testCountByLastName() {
        when(customerRepository.countByLastName("Perez")).thenReturn(2L);
        assertEquals(2L, customerService.countByLastName("Perez"));
    }

    @Test
    // Prueba para obtener estadísticas de clientes
    void testGetStatistics() {
        when(customerRepository.count()).thenReturn(2L);
        when(customerRepository.countByEmailDomainNative("gmail.com")).thenReturn(1L);
        CustomerService.CustomerStats stats = customerService.getStatistics();
        assertEquals(2L, stats.getTotalCustomers());
        assertEquals(1L, stats.getGmailUsers());
    }
}
