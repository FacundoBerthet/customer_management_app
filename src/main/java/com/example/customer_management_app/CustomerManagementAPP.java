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

// Swagger/OpenAPI annotations
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;



@SpringBootApplication // Marca esta como aplicación Spring Boot principal
@RestController // Marca esta clase como un controlador REST
@RequestMapping("/api/customers") // Define la ruta base para las operaciones de cliente
@Tag(name = "Customers", description = "CRUD operations and customer queries")
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
  @Operation(summary = "Get all customers", description = "Retrieve the full list of customers")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "List of customers returned successfully")
  })
  @GetMapping 
  public List<Customer> getAllCustomers() {
    return customerService.getAllCustomers(); // Retorna todos los clientes
  }

  // Obtener un cliente por ID - /api/customers/{id}
  @Operation(summary = "Get customer by ID", description = "Retrieve a single customer by its unique identifier")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Customer found"),
    @ApiResponse(responseCode = "404", description = "Customer not found")
  })
  @GetMapping("/{id}") 
  public ResponseEntity<Customer> getCustomerById(@Parameter(description = "Customer ID") @PathVariable Long id) {
    Optional<Customer> customer = customerService.getCustomerById(id);
    
    if (customer.isPresent()) {
        return ResponseEntity.ok(customer.get()); // 200 OK
    } else {
        return ResponseEntity.notFound().build(); // 404 Not Found
    }
  }

  // Buscar clientes por término de búsqueda - /api/customers/search/{searchTerm}
  @Operation(summary = "Search customers", description = "Search by name, last name, email, or phone containing the given term")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Matching customers returned successfully")
  })
  @GetMapping("/search/{searchTerm}")
  public List<Customer> searchCustomers(@Parameter(description = "Search term") @PathVariable String searchTerm) {
    return customerService.searchCustomers(searchTerm);
  }

  // Buscar por nombre - /api/customers/search/firstname/{firstName}
  @Operation(summary = "Search by first name", description = "Find customers whose first name contains the given value (case-insensitive)")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Matching customers returned successfully")
  })
  @GetMapping("/search/firstname/{firstName}")
  public List<Customer> getCustomersByFirstName(@Parameter(description = "First name to search") @PathVariable String firstName) {
    return customerService.searchCustomers(firstName);
  }

  // Buscar por apellido - /api/customers/search/lastname/{lastName}
  @Operation(summary = "Search by last name", description = "Find customers whose last name contains the given value (case-insensitive)")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Matching customers returned successfully")
  })
  @GetMapping("/search/lastname/{lastName}")
  public List<Customer> getCustomersByLastName(@Parameter(description = "Last name to search") @PathVariable String lastName) {
    return customerService.searchCustomers(lastName);
  }

  // Buscar por nombre que contenga una cadena - /api/customers/search/contains/{name}
  @Operation(summary = "Search by name contains", description = "Find customers where first or last name contains the given text (case-insensitive)")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Matching customers returned successfully")
  })
  @GetMapping("/search/contains/{name}")
  public List<Customer> getCustomersByNameContains(@Parameter(description = "Text to search in name or last name") @PathVariable String name) {
    return customerService.searchCustomers(name);
  }

  // Verificar si un email existe - /api/customers/exists/email/{email}
  @Operation(summary = "Check if email exists", description = "Returns true if a customer with the given email exists")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Result returned successfully")
  })
  @GetMapping("/exists/email/{email}")
  public ResponseEntity<Boolean> existsByEmail(@Parameter(description = "Email to check existence") @PathVariable String email) {
    boolean exists = customerService.existsByEmail(email);
    return ResponseEntity.ok(exists); // 200 OK con el resultado booleano
  }

  // Contar clientes por apellido - /api/customers/count/lastname/{lastName}
  @Operation(summary = "Count by last name", description = "Count how many customers share the given last name")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Count returned successfully")
  })
  @GetMapping("/count/lastname/{lastName}")
  public ResponseEntity<Long> countByLastName(@Parameter(description = "Last name to count") @PathVariable String lastName) {
    long count = customerService.countByLastName(lastName);
    return ResponseEntity.ok(count);
  }

  // Estadisticas de clientes - /api/customers/stats
  @Operation(summary = "Get customer statistics", description = "Aggregate statistics about customers")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Statistics returned successfully")
  })
  @GetMapping("/stats")
  public ResponseEntity<CustomerService.CustomerStats> getStats() {
    CustomerService.CustomerStats stats = customerService.getStatistics();
    return ResponseEntity.ok(stats);
  }


  // ===========================================================================
  // MANEJO DE SOLICITUDES POST
  // ==========================================================================

  // Crear un nuevo cliente - /api/customers
  @Operation(summary = "Create customer", description = "Create a new customer")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Customer created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid input data")
  })
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
  // ==========================================================================

  // Actualizar un cliente existente - /api/customers/{id}
  @Operation(summary = "Update customer", description = "Update an existing customer by ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
    @ApiResponse(responseCode = "404", description = "Customer not found"),
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred")
  })
  @PutMapping("/{id}")
  public ResponseEntity<Customer> updateCustomer(@Parameter(description = "Customer ID") @PathVariable Long id, @Valid @RequestBody Customer customerDetails) {
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
  // ==========================================================================

  // Eliminar un cliente - /api/customers/{id}
  @Operation(summary = "Delete customer", description = "Delete a customer by ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Customer not found"),
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCustomer(@Parameter(description = "Customer ID") @PathVariable Long id) {
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