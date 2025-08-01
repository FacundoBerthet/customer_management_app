package com.example.customer_management_app;

import java.util.List; // importar la clase List
import java.util.Optional; // importar la clase Optional

import org.springframework.data.repository.CrudRepository; // Importar CrudRepository para operaciones CRUD
import org.springframework.data.jpa.repository.Query; // Importar Query para consultas personalizadas
import org.springframework.data.repository.query.Param; // Importar Param para parámetros en consultas

public interface CustomerRepository extends CrudRepository<Customer, Long> {

  // QUERIES AUTOMÁTICAS
  // Spring Data JPA generará automáticamente las consultas basadas en el nombre del método
  Customer findById(long id);
  List<Customer> findByFirstName(String firstName);
  List<Customer> findByLastName(String lastName);   
  Optional<Customer> findByEmail(String email);  // Retorna Optional.empty() si no encuentra
  List<Customer> findByFirstNameContaining(String firstName); // Búsqueda que contenga (LIKE %texto%)
  List<Customer> findByLastNameContaining(String lastName); // Búsqueda que contenga (LIKE %texto%)
  List<Customer> findByFirstNameIgnoreCase(String firstName); // Búsqueda que ignore mayúsculas/minúsculas
  List<Customer> findByFirstNameContainingAndLastNameContaining(String firstName, String lastName);// Búsqueda por múltiples criterios
  boolean existsByEmail(String email);// Verificar si existe
  long countByLastName(String lastName);// Contar registros


  // QUERIES NATIVAS
  // Sql nativo para consultas más complejas

  // Buscar clientes ordenados por fecha de creación
  @Query(value = "SELECT * FROM customer ORDER BY created_at DESC", nativeQuery = true)
  List<Customer> findAllOrderByCreatedAtDesc();
  
  // Contar clientes por dominio de email
  @Query(value = "SELECT COUNT(*) FROM customer WHERE email LIKE %:domain%", nativeQuery = true)
  long countByEmailDomainNative(@Param("domain") String domain);

}