package com.example.customer_management_app;

import org.slf4j.Logger;// Importar la clase Logger para logging
import org.slf4j.LoggerFactory;// Importar la clase LoggerFactory para crear instancias de Logger
import org.springframework.beans.factory.annotation.Autowired; // Importar la anotación @Autowired para inyección de dependencias
import org.springframework.stereotype.Service; // Importar la anotación @Service para marcar esta clase como un servicio de Spring
import org.springframework.transaction.annotation.Transactional; // Importar la anotación @Transactional para manejar transacciones

import java.time.LocalDateTime; // Importar la clase LocalDateTime para manejar fechas y horas
import java.util.List; // Importar la clase List para manejar colecciones de clientes
import java.util.Optional; // Importar la clase Optional para manejar valores que pueden estar ausentes

/**
 * Implementación del servicio de gestión de clientes.
 * 
 * ¿Por qué @Service?
 * - Marca esta clase como un Bean de Spring
 * - Indica que contiene lógica de negocio
 * - Spring la inyectará automáticamente donde se necesite
 * 
 * ¿Por qué @Transactional a nivel de clase?
 * - Todas las operaciones del servicio son transaccionales por defecto
 * - Si algo falla, se hace rollback automático
 * - Garantiza consistencia de datos
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    // Logger para registrar información, advertencias y errores
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class); // Logger para esta clase

    /**
     * ¿Por qué @Autowired en el Repository?
     * - Spring inyecta automáticamente la implementación
     * - No necesitamos instanciar manualmente
     * - Facilita testing (podemos inyectar mocks)
     */
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * ¿Por qué @Transactional(readOnly = true)?
     * - Optimización: Le dice a la Base de Datos que es solo lectura
     * - Mejor performance en consultas
     * - Evita locks innecesarios
     */
    @Override  
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        log.debug("Retrieving all customers");
        return (List<Customer>) customerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(Long id) {
        log.debug("Retrieving customer with id: {}", id); // Para desarrollo
        
        /**
         * ¿Por qué validar el parámetro?
         * - Fail-fast: Detectar errores temprano
         * - Mejor experiencia de debugging
         * - Evitar comportamientos extraños en la BD
         */
        if (id == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        
        return customerRepository.findById(id);
    }

    @Override
    public Customer createCustomer(Customer customer) {
        log.info("Creating new customer: {}", customer.getEmail()); // Para producción
        
        /**
         * ¿Por qué estas validaciones aquí y no en el Controller?
         * - Lógica de NEGOCIO, no de presentación
         * - Se aplica sin importar de dónde venga la petición (REST, GraphQL, etc.)
         * - Centralizada: Un solo lugar para cambiar reglas
         */
        validateCustomerForCreation(customer);
        
        /**
         * ¿Por qué verificar email duplicado aquí?
         * - Regla de negocio: No emails duplicados
         * - @Column(unique=true) lanza exception de BD, esto es más claro
         * - Podemos dar un mensaje de error específico
         */
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + customer.getEmail());
        }
        
        /**
         * ¿Por qué establecer timestamps aquí?
         * - Lógica de negocio: Queremos saber CUÁNDO se creó
         * - Consistencia: Siempre usar la hora del servidor
         * - No depender de que el cliente envíe estos datos
         */
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        
        Customer savedCustomer = customerRepository.save(customer);
        
        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return savedCustomer;
    }

    @Override
    public Customer updateCustomer(Long id, Customer customerUpdates) {
        log.info("Updating customer with ID: {}", id);
        
        /**
         * ¿Por qué buscar el cliente existente primero?
         * - Verificar que existe antes de actualizar
         * - Preservar datos que no se están actualizando
         * - Mejor control de errores
         */
        Customer existingCustomer = getCustomerById(id)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
        
        /**
         * ¿Por qué verificar email duplicado en updates?
         * - Si cambia el email, debe seguir siendo único
         * - Pero NO debe fallar si mantiene el mismo email
         */
        if (!existingCustomer.getEmail().equals(customerUpdates.getEmail()) 
            && customerRepository.existsByEmail(customerUpdates.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + customerUpdates.getEmail());
        }
        
        /**
         * ¿Por qué actualizar campo por campo?
         * - Control total sobre qué se actualiza
         * - Preservar datos sensibles (ID, createdAt)
         * - Validar cada cambio si es necesario
         */
        existingCustomer.setFirstName(customerUpdates.getFirstName());
        existingCustomer.setLastName(customerUpdates.getLastName());
        existingCustomer.setEmail(customerUpdates.getEmail());
        existingCustomer.setPhone(customerUpdates.getPhone());
        existingCustomer.setAddress(customerUpdates.getAddress());
        existingCustomer.setUpdatedAt(LocalDateTime.now()); // Timestamp automático
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        
        log.info("Customer updated successfully: {}", updatedCustomer.getId());
        return updatedCustomer;
    }

    @Override
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        
        /**
         * ¿Por qué verificar existencia antes de eliminar?
         * - Dar un error claro si no existe
         * - Evitar confusión ("¿se eliminó o no existía?")
         * - Logging apropiado
         */
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found with ID: " + id);
        }
        
        customerRepository.deleteById(id);
        log.info("Customer deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> searchCustomers(String searchTerm) {
        log.debug("Searching customers with term: {}", searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCustomers();
        }
        
        /**
         * ¿Por qué usar el método que ya existe en el Repository?
         * - Reutilización de código
         * - DRY (Don't Repeat Yourself)
         * - Si cambia la lógica de búsqueda, solo cambiamos un lugar
         */
        return customerRepository.findByFirstNameContainingOrLastNameContaining(
            searchTerm.trim(), searchTerm.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        log.debug("Checking if email exists: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return customerRepository.existsByEmail(email.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public long countByLastName(String lastName) {
        log.debug("Counting customers with last name: {}", lastName);
        
        if (lastName == null || lastName.trim().isEmpty()) {
            return 0;
        }
        
        return customerRepository.countByLastName(lastName.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerStats getStatistics() {
        log.debug("Generating customer statistics");
        
        /**
         * ¿Por qué calcular estadísticas en el Service?
         * - Lógica de negocio: Combinar múltiples consultas
         * - Reutilizable desde diferentes controladores
         * - Transaccional: Datos consistentes
         */
        long totalCustomers = customerRepository.count();
        long gmailUsers = customerRepository.countByEmailDomainNative("gmail.com");
        
        return new CustomerStats(totalCustomers, gmailUsers);
    }

    /**
     * Método privado para validaciones de negocio.
     * 
     * ¿Por qué un método separado?
     * - Single Responsibility: Cada método tiene una función específica
     * - Reutilizable: Podemos usarlo en create y update
     * - Testeable: Podemos testear las validaciones por separado
     * - Mantenible: Fácil agregar/quitar validaciones
     */
    private void validateCustomerForCreation(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        
        if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        
        if (customer.getLastName() == null || customer.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        /**
         * ¿Por qué validar formato de email aquí también?
         * - Doble verificación: @Email en el entity puede fallar
         * - Lógica de negocio: Queremos emails válidos
         * - Mensaje de error más claro
         */
        if (!customer.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}