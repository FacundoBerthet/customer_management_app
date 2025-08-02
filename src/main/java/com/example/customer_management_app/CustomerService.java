package com.example.customer_management_app;

import java.util.List;// Importar la clase List
import java.util.Optional; // Importar la clase Optional

/**
 * Service interface para la gestión de clientes.
 * Define el contrato de operaciones de negocio para Customer.
 * 
 * ¿Por qué una interfaz?
 * - Principio de Inversión de Dependencias (SOLID)
 * - Facilita testing con mocks
 * - Permite múltiples implementaciones
 * - Abstrae la lógica de negocio del controlador
 */

public interface CustomerService {

    /**
     * Obtiene todos los clientes del sistema.
     * @return Lista de todos los clientes
     */
    List<Customer> getAllCustomers();

    /**
     * Busca un cliente por su ID.
     * @param id ID del cliente
     * @return Optional con el cliente si existe
     */
    Optional<Customer> getCustomerById(Long id);

    /**
     * Crea un nuevo cliente en el sistema.
     * Incluye validaciones de negocio como verificar email duplicado.
     * @param customer Datos del cliente a crear
     * @return Cliente creado con ID asignado
     * @throws IllegalArgumentException si el email ya existe
     */
    Customer createCustomer(Customer customer);

    /**
     * Actualiza un cliente existente.
     * @param id ID del cliente a actualizar
     * @param customerUpdates Datos actualizados
     * @return Cliente actualizado
     * @throws IllegalArgumentException si el cliente no existe
     */
    Customer updateCustomer(Long id, Customer customerUpdates);

    /**
     * Elimina un cliente del sistema.
     * @param id ID del cliente a eliminar
     * @throws IllegalArgumentException si el cliente no existe
     */
    void deleteCustomer(Long id);

    /**
     * Busca clientes por término de búsqueda.
     * Busca en nombre, apellido y email.
     * @param searchTerm Término de búsqueda
     * @return Lista de clientes que coinciden
     */
    List<Customer> searchCustomers(String searchTerm);

    /**
     * Verifica si un email ya está registrado.
     * @param email Email a verificar
     * @return true si el email existe
     */
    boolean existsByEmail(String email);

    /**
     * Cuenta clientes por apellido.
     * @param lastName Apellido a contar
     * @return Número de clientes con ese apellido
     */
    long countByLastName(String lastName);

    /**
     * Obtiene estadísticas del sistema.
     * @return Objeto con estadísticas de clientes
     */
    CustomerStats getStatistics();

    /**
     * Clase interna para estadísticas.
     * ¿Por qué aquí? Porque está estrechamente relacionada con el servicio.
     */
    class CustomerStats {
        private final long totalCustomers;
        private final long gmailUsers;

        public CustomerStats(long totalCustomers, long gmailUsers) {
            this.totalCustomers = totalCustomers;
            this.gmailUsers = gmailUsers;
        }

        // Getters
        public long getTotalCustomers() { return totalCustomers; }
        public long getGmailUsers() { return gmailUsers; }

    }
}