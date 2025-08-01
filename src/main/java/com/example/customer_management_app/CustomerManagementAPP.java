package com.example.customer_management_app;

import org.springframework.beans.factory.annotation.Autowired;// Iportar la anotación @Autowired para inyección de dependencias
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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

  // Manejo de solicitudes GET
  
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


    // Manejo de solicitudes POST

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


    // Manejo de solicitudes PUT
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

  
}