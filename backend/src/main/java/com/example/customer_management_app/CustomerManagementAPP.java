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
import io.swagger.v3.oas.annotations.Hidden; // Ocultar endpoints en la documentación de Swagger/OpenAPI

// Importo DTOs y el mapper para no exponer la entidad directamente en el API
import com.example.customer_management_app.dto.CustomerResponse;
import com.example.customer_management_app.dto.PageResponse;
import com.example.customer_management_app.mapper.CustomerMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest; // Importo PageRequest para construir Pageable con límite de tamaño
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject; // Para documentar Pageable correctamente en OpenAPI


@SpringBootApplication // Marca esta como aplicación Spring Boot principal
@RestController // Marca esta clase como un controlador REST
@RequestMapping("/api/customers") // Define la ruta base para las operaciones de cliente
@Tag(name = "Customers", description = "CRUD operations and customer queries. Standard error format: ErrorResponse (timestamp, path, status, error, message).")
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
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ErrorResponse.class),
        examples = {
          @ExampleObject(
            name = "Not Found",
            summary = "Customer not found",
            value = "{\n  \"timestamp\": \"2025-08-13T10:00:00Z\",\n  \"path\": \"/api/customers/999\",\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Customer not found\"\n}"
          )
        }
      )
    )
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
  // DEPRECADO: usar /api/customers/search/page?q=... con paginación y ordenamiento
  @Hidden // Oculto en Swagger para no ensuciar la documentación
  @Deprecated // Mantengo compatibilidad, pero indico que no debe usarse en nuevas integraciones
  @Operation(summary = "Search customers (DEPRECATED)", description = "Deprecated. Use GET /api/customers/search/page?q=... (supports pagination and sorting).", deprecated = true)
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Matching customers returned successfully")
  })
  @GetMapping("/search/{searchTerm}")
  public List<CustomerResponse> searchCustomers(@Parameter(description = "Search term") @PathVariable String searchTerm) {
    return CustomerMapper.toResponseList(customerService.searchCustomers(searchTerm));
  }

  // Buscar por nombre - /api/customers/search/firstname/{firstName}
  // DEPRECADO: usar /api/customers/search/page?q=... con paginación y ordenamiento
  @Hidden // Oculto en Swagger
  @Deprecated
  @Operation(summary = "Search by first name (DEPRECATED)", description = "Deprecated. Use GET /api/customers/search/page?q=... (supports pagination and sorting).", deprecated = true)
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Matching customers returned successfully")
  })
  @GetMapping("/search/firstname/{firstName}")
  public List<CustomerResponse> getCustomersByFirstName(@Parameter(description = "First name to search") @PathVariable String firstName) {
    return CustomerMapper.toResponseList(customerService.searchCustomers(firstName));
  }

  // Buscar por apellido - /api/customers/search/lastname/{lastName}
  // DEPRECADO: usar /api/customers/search/page?q=... con paginación y ordenamiento
  @Hidden // Oculto en Swagger
  @Deprecated
  @Operation(summary = "Search by last name (DEPRECATED)", description = "Deprecated. Use GET /api/customers/search/page?q=... (supports pagination and sorting).", deprecated = true)
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Matching customers returned successfully")
  })
  @GetMapping("/search/lastname/{lastName}")
  public List<CustomerResponse> getCustomersByLastName(@Parameter(description = "Last name to search") @PathVariable String lastName) {
    return CustomerMapper.toResponseList(customerService.searchCustomers(lastName));
  }

  // Buscar por nombre que contenga una cadena - /api/customers/search/contains/{name}
  // DEPRECADO: usar /api/customers/search/page?q=... con paginación y ordenamiento
  @Hidden // Oculto en Swagger
  @Deprecated
  @Operation(summary = "Search by name contains (DEPRECATED)", description = "Deprecated. Use GET /api/customers/search/page?q=... (supports pagination and sorting).", deprecated = true)
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
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ErrorResponse.class),
        examples = {
          @ExampleObject(
            name = "Bad Request",
            summary = "Validation error",
            value = "{\n  \"timestamp\": \"2025-08-13T10:00:00Z\",\n  \"path\": \"/api/customers\",\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid request payload\"\n}"
          )
        }
      )
    ),
    @ApiResponse(responseCode = "409", description = "Email already exists",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ErrorResponse.class),
        examples = {
          @ExampleObject(
            name = "Conflict",
            summary = "Email already exists",
            value = "{\n  \"timestamp\": \"2025-08-13T10:00:00Z\",\n  \"path\": \"/api/customers\",\n  \"status\": 409,\n  \"error\": \"Conflict\",\n  \"message\": \"Email already exists\"\n}"
          )
        }
      )
    )
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
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ErrorResponse.class),
        examples = {
          @ExampleObject(
            name = "Not Found",
            summary = "Customer not found",
            value = "{\n  \"timestamp\": \"2025-08-13T10:00:00Z\",\n  \"path\": \"/api/customers/999\",\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Customer not found\"\n}"
          )
        }
      )
    ),
    @ApiResponse(responseCode = "409", description = "Email already exists",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ErrorResponse.class),
        examples = {
          @ExampleObject(
            name = "Conflict",
            summary = "Email already exists",
            value = "{\n  \"timestamp\": \"2025-08-13T10:00:00Z\",\n  \"path\": \"/api/customers/1\",\n  \"status\": 409,\n  \"error\": \"Conflict\",\n  \"message\": \"Email already exists\"\n}"
          )
        }
      )
    ),
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ErrorResponse.class),
        examples = {
          @ExampleObject(
            name = "Internal Server Error",
            summary = "Unexpected error",
            value = "{\n  \"timestamp\": \"2025-08-13T10:00:00Z\",\n  \"path\": \"/api/customers/1\",\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error occurred\"\n}"
          )
        }
      )
    )
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
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ErrorResponse.class),
        examples = {
          @ExampleObject(
            name = "Not Found",
            summary = "Customer not found",
            value = "{\n  \"timestamp\": \"2025-08-13T10:00:00Z\",\n  \"path\": \"/api/customers/999\",\n  \"status\": 404,\n  \"error\": \"Not Found\",\n  \"message\": \"Customer not found\"\n}"
          )
        }
      )
    ),
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ErrorResponse.class),
        examples = {
          @ExampleObject(
            name = "Internal Server Error",
            summary = "Unexpected error",
            value = "{\n  \"timestamp\": \"2025-08-13T10:00:00Z\",\n  \"path\": \"/api/customers/1\",\n  \"status\": 500,\n  \"error\": \"Internal Server Error\",\n  \"message\": \"Unexpected error occurred\"\n}"
          )
        }
      )
    )
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
  @Operation(
    summary = "Get customers (paged)",
    description = "Retrieve customers with pagination and sorting.\n\n" +
                  "Notes:\n" +
                  "- Max page size: 50 (larger values are capped).\n" +
                  "- Default sort: id,DESC.\n" +
                  "- Supported params: page (0..N), size (1..50), sort (field,ASC|DESC).\n\n" +
                  "Examples:\n" +
                  "- GET /api/customers/page?page=0&size=10\n" +
                  "- GET /api/customers/page?page=1&size=20&sort=lastName,ASC\n" +
                  "- GET /api/customers/page?sort=lastName,ASC&sort=firstName,DESC"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Page of customers returned successfully",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = com.example.customer_management_app.dto.PageResponse.class),
        examples = {
          @ExampleObject(
            name = "Basic paginated list",
            summary = "First page, size 10, default sort id,DESC",
            value = "{\n  \"content\": [\n    {\n      \"id\": 15,\n      \"firstName\": \"Ana\",\n      \"lastName\": \"García\",\n      \"email\": \"ana.garcia@example.com\",\n      \"phone\": \"123-4567\",\n      \"address\": \"123 Main St\",\n      \"createdAt\": \"2025-08-10T12:34:56\",\n      \"updatedAt\": \"2025-08-12T08:00:00\"\n    }\n  ],\n  \"page\": 0,\n  \"size\": 10,\n  \"totalElements\": 42,\n  \"totalPages\": 5,\n  \"first\": true,\n  \"last\": false\n}"
          )
        }
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid paging parameters",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ErrorResponse.class),
        examples = {
          @ExampleObject(
            name = "Bad Request",
            summary = "Invalid size or sort",
            value = "{\n  \"timestamp\": \"2025-08-13T10:00:00Z\",\n  \"path\": \"/api/customers/page\",\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid paging parameters: size must be between 1 and 50; sort format is field,ASC|DESC\"\n}"
          )
        }
      )
    )
  })
  @GetMapping("/page")
  public PageResponse<CustomerResponse> getCustomersPaged(
      @ParameterObject
      @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

    // Limito el tamaño máximo permitido (size > 50 -> 50)
    int cappedSize = Math.min(pageable.getPageSize(), 50);
    Pageable effective = (pageable.getPageSize() != cappedSize)
        ? PageRequest.of(pageable.getPageNumber(), cappedSize, pageable.getSort())
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

  @Operation(
    summary = "Search customers (paged)",
    description = "Search by first name, last name, email, phone or address with pagination and sorting.\n\n" +
                  "Notes:\n" +
                  "- Max page size: 50 (larger values are capped).\n" +
                  "- Default sort: id,DESC.\n" +
                  "- Supported params: q (search text), page (0..N), size (1..50), sort (field,ASC|DESC).\n\n" +
                  "Examples:\n" +
                  "- GET /api/customers/search/page?q=john&page=0&size=10\n" +
                  "- GET /api/customers/search/page?q=gmail.com&sort=lastName,ASC\n" +
                  "- GET /api/customers/search/page?q=742%20Evergreen\n" +
                  "- GET /api/customers/search/page?q=123-4567&sort=firstName,DESC"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Page of customers returned successfully",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = com.example.customer_management_app.dto.PageResponse.class),
        examples = {
          @ExampleObject(
            name = "Paginated search",
            summary = "Results for q=john, first page",
            value = "{\n  \"content\": [\n    {\n      \"id\": 7,\n      \"firstName\": \"John\",\n      \"lastName\": \"Doe\",\n      \"email\": \"john.doe@example.com\",\n      \"phone\": \"123-4567\",\n      \"address\": \"742 Evergreen Terrace\",\n      \"createdAt\": \"2025-08-09T10:00:00\",\n      \"updatedAt\": \"2025-08-12T08:30:00\"\n    }\n  ],\n  \"page\": 0,\n  \"size\": 10,\n  \"totalElements\": 3,\n  \"totalPages\": 1,\n  \"first\": true,\n  \"last\": true\n}"
          )
        }
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid paging parameters",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ErrorResponse.class),
        examples = {
          @ExampleObject(
            name = "Bad Request",
            summary = "Invalid size or sort",
            value = "{\n  \"timestamp\": \"2025-08-13T10:00:00Z\",\n  \"path\": \"/api/customers/search/page\",\n  \"status\": 400,\n  \"error\": \"Bad Request\",\n  \"message\": \"Invalid paging parameters: size must be between 1 and 50; sort format is field,ASC|DESC\"\n}"
          )
        }
      )
    )
  })
  @GetMapping("/search/page")
  public PageResponse<CustomerResponse> searchCustomersPaged(
      @Parameter(description = "Search term", example = "john") @RequestParam("q") String q,
      @ParameterObject
      @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

    int cappedSize = Math.min(pageable.getPageSize(), 50);
    Pageable effective = (pageable.getPageSize() != cappedSize)
        ? PageRequest.of(pageable.getPageNumber(), cappedSize, pageable.getSort())
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

  // Buscar cliente por email exacto - /api/customers/by-email
  @Operation(summary = "Get customer by email", description = "Lookup a customer by exact email")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Customer found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.example.customer_management_app.dto.CustomerResponse.class))),
    @ApiResponse(responseCode = "404", description = "Customer not found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/by-email")
  public ResponseEntity<CustomerResponse> getByEmail(@Parameter(description = "Exact email", example = "john.doe@example.com") @RequestParam("email") String email) {
    // Normalizo y delego al Service
    return customerService.getByEmail(email)
        .map(c -> ResponseEntity.ok(CustomerMapper.toResponse(c)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Buscar cliente por teléfono exacto - /api/customers/by-phone
  @Operation(summary = "Get customer by phone", description = "Lookup a customer by exact phone number")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Customer found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.example.customer_management_app.dto.CustomerResponse.class))),
    @ApiResponse(responseCode = "404", description = "Customer not found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/by-phone")
  public ResponseEntity<CustomerResponse> getByPhone(@Parameter(description = "Exact phone", example = "123-4567") @RequestParam("phone") String phone) {
    // Normalizo y delego al Service
    return customerService.getByPhone(phone)
        .map(c -> ResponseEntity.ok(CustomerMapper.toResponse(c)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}