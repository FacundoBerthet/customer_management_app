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
import io.swagger.v3.oas.annotations.parameters.RequestBody; // Documentar el cuerpo de la solicitud
import io.swagger.v3.oas.annotations.media.Content; // Especificar el tipo de contenido
import io.swagger.v3.oas.annotations.media.Schema; // Referenciar el esquema del modelo
import io.swagger.v3.oas.annotations.media.ExampleObject; // Incluir ejemplos de payload
import io.swagger.v3.oas.annotations.media.ArraySchema; // Para documentar listas/arrays en OpenAPI

// Importo DTOs y el mapper para no exponer la entidad directamente en el API
import com.example.customer_management_app.dto.CustomerResponse;
import com.example.customer_management_app.dto.PageResponse;
import com.example.customer_management_app.mapper.CustomerMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest; // Importo PageRequest para construir Pageable con límite de tamaño
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestParam;


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

  // ==========================================================================
  // MANEJO DE SOLICITUDES GET
  // ==========================================================================
  
  // Obtener todos los clientes - /api/customers
  @Operation(summary = "Get all customers", description = "Retrieve the full list of customers")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "List of customers returned successfully",
      content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.example.customer_management_app.dto.CustomerResponse.class))))
  })
  @GetMapping 
  public List<CustomerResponse> getAllCustomers() {
    // Mapeo entidad -> DTO para no exponer la entidad JPA
    return CustomerMapper.toResponseList(customerService.getAllCustomers());
  }

  // Obtener un cliente por ID - /api/customers/{id}
  @Operation(summary = "Get customer by ID", description = "Retrieve a single customer by its unique identifier")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Customer found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.example.customer_management_app.dto.CustomerResponse.class))),
    @ApiResponse(responseCode = "404", description = "Customer not found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/{id}") 
  public ResponseEntity<com.example.customer_management_app.dto.CustomerResponse> getCustomerById(@Parameter(description = "Customer ID") @PathVariable Long id) {
    Optional<Customer> customer = customerService.getCustomerById(id);
    
    if (customer.isPresent()) {
        return ResponseEntity.ok(com.example.customer_management_app.mapper.CustomerMapper.toResponse(customer.get())); // 200 OK
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
  public List<CustomerResponse> searchCustomers(@Parameter(description = "Search term") @PathVariable String searchTerm) {
    return CustomerMapper.toResponseList(customerService.searchCustomers(searchTerm));
  }

  // Buscar por nombre - /api/customers/search/firstname/{firstName}
  @Operation(summary = "Search by first name", description = "Find customers whose first name contains the given value (case-insensitive)")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Matching customers returned successfully")
  })
  @GetMapping("/search/firstname/{firstName}")
  public List<CustomerResponse> getCustomersByFirstName(@Parameter(description = "First name to search") @PathVariable String firstName) {
    return CustomerMapper.toResponseList(customerService.searchCustomers(firstName));
  }

  // Buscar por apellido - /api/customers/search/lastname/{lastName}
  @Operation(summary = "Search by last name", description = "Find customers whose last name contains the given value (case-insensitive)")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Matching customers returned successfully")
  })
  @GetMapping("/search/lastname/{lastName}")
  public List<CustomerResponse> getCustomersByLastName(@Parameter(description = "Last name to search") @PathVariable String lastName) {
    return CustomerMapper.toResponseList(customerService.searchCustomers(lastName));
  }

  // Buscar por nombre que contenga una cadena - /api/customers/search/contains/{name}
  @Operation(summary = "Search by name contains", description = "Find customers where first or last name contains the given text (case-insensitive)")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Matching customers returned successfully")
  })
  @GetMapping("/search/contains/{name}")
  public List<CustomerResponse> getCustomersByNameContains(@Parameter(description = "Text to search in name or last name") @PathVariable String name) {
    return CustomerMapper.toResponseList(customerService.searchCustomers(name));
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


  // ==========================================================================
  // MANEJO DE SOLICITUDES POST
  // ==========================================================================

  // Crear un nuevo cliente - /api/customers
  @Operation(summary = "Create customer", description = "Create a new customer")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Customer created successfully",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.example.customer_management_app.dto.CustomerResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid input data",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(responseCode = "409", description = "Email already exists",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RequestBody(
    description = "Customer data to create",
    required = true,
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = com.example.customer_management_app.dto.CustomerRequest.class),
      examples = {
        @ExampleObject(
          name = "Minimal",
          summary = "Required fields only",
          value = "{\n  \"firstName\": \"John\",\n  \"lastName\": \"Doe\",\n  \"email\": \"john.doe@example.com\"\n}"
        ),
        @ExampleObject(
          name = "With optional fields",
          summary = "Includes phone and address",
          value = "{\n  \"firstName\": \"Ana\",\n  \"lastName\": \"García\",\n  \"email\": \"ana.garcia@example.com\",\n  \"phone\": \"123-4567\",\n  \"address\": \"123 Main St, Springfield\"\n}"
        )
      }
    )
  )
  @PostMapping
  public ResponseEntity<com.example.customer_management_app.dto.CustomerResponse> createCustomer(@Valid @org.springframework.web.bind.annotation.RequestBody com.example.customer_management_app.dto.CustomerRequest request) {
      try {
          Customer savedCustomer = customerService.createCustomer(com.example.customer_management_app.mapper.CustomerMapper.fromRequest(request));
          return ResponseEntity.status(HttpStatus.CREATED).body(com.example.customer_management_app.mapper.CustomerMapper.toResponse(savedCustomer)); // 201 Created
      } catch (DuplicateEmailException e) {
          return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict (manejado globalmente)
      } catch (Exception e) {
          return ResponseEntity.badRequest().build(); // 400 Bad Request (validación global)
      }
  }


  // ==========================================================================
  // MANEJO DE SOLICITUDES PUT
  // ==========================================================================

  // Actualizar un cliente existente - /api/customers/{id}
  @Operation(summary = "Update customer", description = "Update an existing customer by ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Customer updated successfully",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.example.customer_management_app.dto.CustomerResponse.class))),
    @ApiResponse(responseCode = "404", description = "Customer not found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(responseCode = "409", description = "Email already exists",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RequestBody(
    description = "Customer data to update (ID provided in path)",
    required = true,
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = com.example.customer_management_app.dto.CustomerRequest.class),
      examples = {
        @ExampleObject(
          name = "Update name",
          summary = "Change first and last name",
          value = "{\n  \"firstName\": \"Jane\",\n  \"lastName\": \"Doe\",\n  \"email\": \"jane.doe@example.com\"\n}"
        ),
        @ExampleObject(
          name = "Update optional fields",
          summary = "Modify phone and address",
          value = "{\n  \"firstName\": \"John\",\n  \"lastName\": \"Doe\",\n  \"email\": \"john.doe@example.com\",\n  \"phone\": \"987-6543\",\n  \"address\": \"742 Evergreen Terrace\"\n}"
        )
      }
    )
  )
  @PutMapping("/{id}")
  public ResponseEntity<com.example.customer_management_app.dto.CustomerResponse> updateCustomer(@Parameter(description = "Customer ID") @PathVariable Long id, @Valid @org.springframework.web.bind.annotation.RequestBody com.example.customer_management_app.dto.CustomerRequest customerDetails) {
    try {
        Customer updatedCustomer = customerService.updateCustomer(id, com.example.customer_management_app.mapper.CustomerMapper.fromRequest(customerDetails));
        return ResponseEntity.ok(com.example.customer_management_app.mapper.CustomerMapper.toResponse(updatedCustomer));
    } catch (DuplicateEmailException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict
    } catch (IllegalArgumentException e) {
        // El Service maneja tanto "no encontrado" como "email duplicado"
        return ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }


  // ==========================================================================
  // MANEJO DE SOLICITUDES DELETE
  // ==========================================================================

  // Eliminar un cliente - /api/customers/{id}
  @Operation(summary = "Delete customer", description = "Delete a customer by ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Customer not found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
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
  
  // ==========================================================================
  // MANEJO DE SOLICITUDES PAGINADAS
  // ==========================================================================

  // Obtener clientes con paginación - /api/customers/page
  @Operation(summary = "Get customers (paged)", description = "Retrieve customers with pagination and sorting (default sort: id,DESC)")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Page of customers returned successfully",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.example.customer_management_app.dto.PageResponse.class)))
  })
  @GetMapping("/page")
  public PageResponse<CustomerResponse> getCustomersPaged(
      @Parameter(description = "Pagination and sorting parameters (page, size, sort)")
      @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
      @RequestParam(value = "size", required = false) Integer sizeOverride) {

    // Limito el tamaño máximo permitido para evitar abusos
    Pageable effective = (sizeOverride != null && sizeOverride > 50)
        ? PageRequest.of(pageable.getPageNumber(), 50, pageable.getSort())
        : pageable;

    Page<Customer> page = customerService.getAllCustomers(effective);
    return new PageResponse<>(
        CustomerMapper.toResponseList(page.getContent()),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast()
    );
  }

  @Operation(summary = "Search customers (paged)", description = "Search by name or last name (default sort: id,DESC)")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Page of customers returned successfully",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.example.customer_management_app.dto.PageResponse.class)))
  })
  @GetMapping("/search/page")
  public PageResponse<CustomerResponse> searchCustomersPaged(
      @Parameter(description = "Search term") @RequestParam("q") String q,
      @Parameter(description = "Pagination and sorting parameters (page, size, sort)")
      @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
      @RequestParam(value = "size", required = false) Integer sizeOverride) {

    // Limito el tamaño máximo permitido
    Pageable effective = (sizeOverride != null && sizeOverride > 50)
        ? PageRequest.of(pageable.getPageNumber(), 50, pageable.getSort())
        : pageable;

    Page<Customer> page = customerService.searchCustomers(q, effective);
    return new PageResponse<>(
        CustomerMapper.toResponseList(page.getContent()),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast()
    );
  }
}