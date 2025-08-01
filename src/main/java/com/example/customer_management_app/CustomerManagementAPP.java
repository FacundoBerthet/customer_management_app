package com.example.customer_management_app;

import org.springframework.beans.factory.annotation.Autowired; // Importar la anotación @Autowired para inyección de dependencias
import org.springframework.http.HttpStatus; // Importar HttpStatus para manejar códigos de estado HTTP
import org.springframework.http.ResponseEntity; // Importar ResponseEntity para respuestas HTTP
import org.springframework.web.bind.annotation.*; // Importar anotaciones de controlador REST
import jakarta.validation.Valid; // Importar la anotación @Valid para validación de datos

import java.util.List; // Importar la clase List
import java.util.Optional; // Importar la clase Optional


@RestController // Marca esta clase como un controlador REST
@RequestMapping("/api/customers") // Define la ruta base para las operaciones de cliente
public class CustomerManagementAPP {

  @Autowired // Inyecta automaticamente el CustomerRepository
  private CustomerRepository customerRepository;


  // MANEJO DE SOLICITUDES GET
  
  // Obtener todos los clientes - /api/customers
  @GetMapping 
  public List<Customer> getAllCustomers() {
    return (List<Customer>) customerRepository.findAll(); // Retorna todos los clientes
  }

  // Obtener un cliente por ID - /api/customers/{id}
  @GetMapping("/{id}") 
  public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
    Optional<Customer> customer = customerRepository.findById(id);
    
    if (customer.isPresent()) {
        return ResponseEntity.ok(customer.get()); // 200 OK
    } else {
        return ResponseEntity.notFound().build(); // 404 Not Found
    }
  }

  // Buscar por nombre - /api/customers/search/firstname/{firstName}
  @GetMapping("/search/firstname/{firstName}")
  public List<Customer> getCustomersByFirstName(@PathVariable String firstName) {
    return customerRepository.findByFirstName(firstName);
  }

  // Buscar por apellido - /api/customers/search/lastname/{lastName}
  @GetMapping("/search/lastname/{lastName}")
  public List<Customer> getCustomersByLastName(@PathVariable String lastName) {
    return customerRepository.findByLastName(lastName);
  }

  // Buscar por nombre que contenga una cadena - /api/customers/search/contains/{name}
  @GetMapping("/search/contains/{name}")
  public List<Customer> getCustomersByNameContains(@PathVariable String name) {
    return customerRepository.findByFirstNameContainingOrLastNameContaining(name, name);
  }

  // Verificar si un email existe - /api/customers/exists/email/{email}
  @GetMapping("/exists/email/{email}")
  public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
    boolean exists = customerRepository.existsByEmail(email);
    return ResponseEntity.ok(exists); // 200 OK con el resultado booleano
  }

  // Contar clientes por apellido - /api/customers/count/lastname/{lastName}
  @GetMapping("/count/lastname/{lastName}")
  public ResponseEntity<Long> countByLastName(@PathVariable String lastName) {
    long count = customerRepository.countByLastName(lastName);
    return ResponseEntity.ok(count);
  }

  // Estadisticas de clientes - /api/customers/stats
  @GetMapping("/stats")
  public ResponseEntity<CustomerStats> getStats() {
        long totalCustomers = customerRepository.count();
        long gmailUsers = customerRepository.countByEmailDomainNative("gmail.com"); // Contar usuarios con dominio gmail

        CustomerStats stats = new CustomerStats(totalCustomers, gmailUsers);
        return ResponseEntity.ok(stats);
    }

  // MANEJO DE SOLICITUDES POST

  // Crear un nuevo cliente - /api/customers
  @PostMapping
  public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
      try {
          Customer savedCustomer = customerRepository.save(customer);
          return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer); // 201 Created
      } catch (Exception e) {
          return ResponseEntity.badRequest().build(); // 400 Bad Request
      }
  }


  // MANEJO DE SOLICITUDES PUT

  // Actualizar un cliente existente - /api/customers/{id}
  @PutMapping("/{id}")
  public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @Valid @RequestBody Customer customerDetails) {
    Optional<Customer> customerOptional = customerRepository.findById(id);
        
      if (customerOptional.isPresent()) {
          Customer customer = customerOptional.get();
            
          // Actualizar campos
          customer.setFirstName(customerDetails.getFirstName());
          customer.setLastName(customerDetails.getLastName());
          customer.setEmail(customerDetails.getEmail());
          customer.setPhone(customerDetails.getPhone());
          customer.setAddress(customerDetails.getAddress());
          
          Customer updatedCustomer = customerRepository.save(customer);
          return ResponseEntity.ok(updatedCustomer); // 200 OK
      } else {
          return ResponseEntity.notFound().build(); // 404 Not Found
      }
  }


  // MANEJO DE SOLICITUDES DELETE

  // Eliminar un cliente - /api/customers/{id}
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
      if (customerRepository.existsById(id)) {
          customerRepository.deleteById(id);
          return ResponseEntity.noContent().build(); // 204 No Content
      } else {
          return ResponseEntity.notFound().build(); // 404 Not Found
      }
  }
  

  // CLASE INTERNA PARA ESTADISTICAS
  public static class CustomerStats {
    private long totalCustomers;
    private long gmailUsers;

    public CustomerStats(long totalCustomers, long gmailUsers) {
      this.totalCustomers = totalCustomers;
      this.gmailUsers = gmailUsers;
    }

    // Getters
    public long getTotalCustomers() {
      return totalCustomers;
    }

    public long getGmailUsers() {
      return gmailUsers;
    }

    // Setters
    public void setTotalCustomers(long totalCustomers) {
      this.totalCustomers = totalCustomers;
    }

    public void setGmailUsers(long gmailUsers) {
      this.gmailUsers = gmailUsers;
    }
  }  
}