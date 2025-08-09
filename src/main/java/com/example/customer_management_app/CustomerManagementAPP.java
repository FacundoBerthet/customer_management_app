package com.example.customer_management_app;

import org.springframework.beans.factory.annotation.Autowired; // Importar la anotación @Autowired para inyección de dependencias
import org.springframework.http.HttpStatus; // Importar HttpStatus para manejar códigos de estado HTTP
import org.springframework.http.ResponseEntity; // Importar ResponseEntity para respuestas HTTP
import org.springframework.web.bind.annotation.*; // Importar anotaciones de controlador REST
import jakarta.validation.Valid; // Importar la anotación @Valid para validación de datos

import org.springframework.boot.SpringApplication; // Importar SpringApplication para iniciar la aplicación
import org.springframework.boot.autoconfigure.SpringBootApplication; // Importar la anotación @SpringBootApplication para marcar la clase principal de la aplicación

import java.util.List; // Importar la clase List
import java.util.Optional; // Importar la clase Optional

@SpringBootApplication // Marca esta como aplicación Spring Boot principal
@RestController // Marca esta clase como un controlador REST
@RequestMapping("/api/customers") // Define la ruta base para las operaciones de cliente
public class CustomerManagementAPP {

  // Método main para ejecutar la aplicación
  public static void main(String[] args) {
    SpringApplication.run(CustomerManagementAPP.class, args);
  }

  @Autowired // Inyecta automaticamente el CustomerService
  private CustomerService customerService;

  // ===========================================================================
  // MANEJO DE SOLICITUDES GET
  // ===========================================================================
  
  // Obtener todos los clientes - /api/customers
  @GetMapping 
  public List<Customer> getAllCustomers() {
    return customerService.getAllCustomers(); // Retorna todos los clientes
  }

  // Obtener un cliente por ID - /api/customers/{id}
  @GetMapping("/{id}") 
  public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
    Optional<Customer> customer = customerService.getCustomerById(id);
    
    if (customer.isPresent()) {
        return ResponseEntity.ok(customer.get()); // 200 OK
    } else {
        return ResponseEntity.notFound().build(); // 404 Not Found
    }
  }

  // Buscar clientes por término de búsqueda - /api/customers/search/{searchTerm}
  @GetMapping("/search/{searchTerm}")
  public List<Customer> searchCustomers(@PathVariable String searchTerm) {
    return customerService.searchCustomers(searchTerm);
  }

  // Buscar por nombre - /api/customers/search/firstname/{firstName}
  @GetMapping("/search/firstname/{firstName}")
  public List<Customer> getCustomersByFirstName(@PathVariable String firstName) {
    return customerService.searchCustomers(firstName);
  }

  // Buscar por apellido - /api/customers/search/lastname/{lastName}
  @GetMapping("/search/lastname/{lastName}")
  public List<Customer> getCustomersByLastName(@PathVariable String lastName) {
    return customerService.searchCustomers(lastName);
  }

  // Buscar por nombre que contenga una cadena - /api/customers/search/contains/{name}
  @GetMapping("/search/contains/{name}")
  public List<Customer> getCustomersByNameContains(@PathVariable String name) {
    return customerService.searchCustomers(name);
  }

  // Verificar si un email existe - /api/customers/exists/email/{email}
  @GetMapping("/exists/email/{email}")
  public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
    boolean exists = customerService.existsByEmail(email);
    return ResponseEntity.ok(exists); // 200 OK con el resultado booleano
  }

  // Contar clientes por apellido - /api/customers/count/lastname/{lastName}
  @GetMapping("/count/lastname/{lastName}")
  public ResponseEntity<Long> countByLastName(@PathVariable String lastName) {
    long count = customerService.countByLastName(lastName);
    return ResponseEntity.ok(count);
  }

  // Estadisticas de clientes - /api/customers/stats
  @GetMapping("/stats")
  public ResponseEntity<CustomerService.CustomerStats> getStats() {
    CustomerService.CustomerStats stats = customerService.getStatistics();
    return ResponseEntity.ok(stats);
  }


  // ===========================================================================
  // MANEJO DE SOLICITUDES POST
  // ===========================================================================

  // Crear un nuevo cliente - /api/customers
  @PostMapping
  public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
      try {
          Customer savedCustomer = customerService.createCustomer(customer);
          return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer); // 201 Created
      } catch (Exception e) {
          return ResponseEntity.badRequest().build(); // 400 Bad Request
      }
  }


  // ===========================================================================
  // MANEJO DE SOLICITUDES PUT
  // ===========================================================================

  // Actualizar un cliente existente - /api/customers/{id}
  @PutMapping("/{id}")
  public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @Valid @RequestBody Customer customerDetails) {
    try {
        Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
        return ResponseEntity.ok(updatedCustomer);
    } catch (IllegalArgumentException e) {
        // El Service maneja tanto "no encontrado" como "email duplicado"
        return ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }


  // ===========================================================================
  // MANEJO DE SOLICITUDES DELETE
  // ===========================================================================

  // Eliminar un cliente - /api/customers/{id}
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
    try {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
        // El Service lanza excepción si no existe
        return ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }  
  } 
}