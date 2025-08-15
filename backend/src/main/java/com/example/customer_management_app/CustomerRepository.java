package com.example.customer_management_app;

import java.util.List; // importar la clase List
import java.util.Optional; // importar la clase Optional

import org.springframework.data.domain.Page; // Para resultados paginados
import org.springframework.data.domain.Pageable; // Para parámetros de paginación
import org.springframework.data.jpa.repository.JpaRepository; // Cambio a JpaRepository para habilitar paginación y ordenamiento
import org.springframework.data.jpa.repository.Query; // Importar Query para consultas personalizadas
import org.springframework.data.repository.query.Param; // Importar Param para parámetros en consultas

public interface CustomerRepository extends JpaRepository<Customer, Long> {

  // QUERIES AUTOMÁTICAS
  // Spring Data JPA generará automáticamente las consultas basadas en el nombre del método
  Optional<Customer> findById(Long id);
  List<Customer> findByFirstName(String firstName);
  List<Customer> findByLastName(String lastName);   
  Optional<Customer> findByEmail(String email);  // Retorna Optional.empty() si no encuentra
  Optional<Customer> findByPhone(String phone);
  List<Customer> findByAddressContaining(String address);
  List<Customer> findByFirstNameContaining(String firstName); // Búsqueda que contenga (LIKE %texto%)
  List<Customer> findByLastNameContaining(String lastName); // Búsqueda que contenga (LIKE %texto%)
  List<Customer> findByFirstNameIgnoreCase(String firstName); // Búsqueda que ignore mayúsculas/minúsculas
  List<Customer> findByFirstNameContainingAndLastNameContaining(String firstName, String lastName);// Búsqueda por múltiples criterios
  List<Customer> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName); // Búsqueda por nombre o apellido
  boolean existsByEmail(String email);// Verificar si existe
  long countByLastName(String lastName);// Contar registros

  // Variante paginada de la búsqueda por nombre o apellido
  Page<Customer> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName, Pageable pageable);

  // Búsqueda paginada unificada (contains, case-insensitive) en múltiples campos
  Page<Customer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrAddressContainingIgnoreCase(
      String firstName,
      String lastName,
      String email,
      String phone,
      String address,
      Pageable pageable);

  // QUERIES NATIVAS
  // Sql nativo para consultas más complejas

  // Buscar clientes ordenados por fecha de creación
  @Query(value = "SELECT * FROM customer ORDER BY created_at DESC", nativeQuery = true)
  List<Customer> findAllOrderByCreatedAtDesc();
  
  // Contar clientes por dominio de email
  @Query(value = "SELECT COUNT(*) FROM customer WHERE email LIKE '%' || :domain || '%'", nativeQuery = true)
  long countByEmailDomainNative(@Param("domain") String domain);
}