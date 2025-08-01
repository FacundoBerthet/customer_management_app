package com.example.customer_management_app;

// Importa la anotación @Entity para marcar esta clase como entidad JPA
import jakarta.persistence.Entity;

// Importa la anotación @GeneratedValue para generar valores automáticamente
import jakarta.persistence.GeneratedValue;

// Importa el enum GenerationType para especificar la estrategia de generación de IDs
import jakarta.persistence.GenerationType;

// Importa la anotación @Id para marcar el campo como clave primaria
import jakarta.persistence.Id;

// Importa la anotación @Column para definir propiedades de la columna en la base de datos
import jakarta.persistence.Column;

// Importa la clase LocalDateTime para manejar fechas y horas
import java.time.LocalDateTime;


@Entity // Marca esta clase como una entidad JPA - se mapeará a una tabla en la base de datos
public class Customer {

  
  @Id // Marca este campo como la clave primaria de la tabla
  @GeneratedValue(strategy=GenerationType.AUTO) // Configura la generación automática del ID - JPA elegirá la mejor estrategia
  private Long id; // Campo para almacenar el ID único del cliente (clave primaria)
  private String firstName; 
  private String lastName;  
  @Column(unique = true) // Email debe ser único
  private String email;
  private String phone;
  private String address;
  @Column(name = "created_at") // Nombre de la columna en la base de datos
  private LocalDateTime createdAt; // Fecha de creación del registro
  @Column(name = "updated_at") // Nombre de la columna en la base de datos
  private LocalDateTime updatedAt; // Fecha de actualización del registro


  // Constructor vacío protegido - requerido por JPA para crear instancias de la entidad
  protected Customer() {}


  // CONSTRUCTOR CON 3 PARÁMETROS (nombre, apellido, email)
  public Customer(String firstName, String lastName, String email) {
    this.firstName = firstName; // Asigna el nombre recibido al campo firstName
    this.lastName = lastName;   // Asigna el apellido recibido al campo lastName
    this.email = email;         // Asigna el email recibido al campo email
    this.phone = null;          // Teléfono queda como null (opcional)
    this.address = null;        // Dirección queda como null (opcional)
    this.createdAt = LocalDateTime.now(); // Asigna la fecha actual al campo createdAt
    this.updatedAt = LocalDateTime.now(); // Asigna la fecha actual al campo updatedAt
  }

  // CONSTRUCTOR CON 5 PARÁMETROS (todos los campos)
  public Customer(String firstName, String lastName, String email, String phone, String address) {
    this.firstName = firstName; // Asigna el nombre recibido al campo firstName
    this.lastName = lastName;   // Asigna el apellido recibido al campo lastName
    this.email = email;         // Asigna el email recibido al campo email
    this.phone = phone;         // Asigna el teléfono recibido al campo phone
    this.address = address;     // Asigna la dirección recibida al campo address
    this.createdAt = LocalDateTime.now(); // Asigna la fecha actual al campo createdAt
    this.updatedAt = LocalDateTime.now(); // Asigna la fecha actual al campo updatedAt
  }


  // METODOS 

  // Sobrescribe el método toString() para representar el objeto como String legible
  @Override
  public String toString() {
    // Retorna una cadena formateada con los datos del cliente
    return String.format(
        "Customer[id=%d, firstName='%s', lastName='%s', email='%s', phone='%s', address='%s']",
        id, firstName, lastName, email, phone, address);
  }

  // GETTERS
  public Long getId() {
    return id; 
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }

  public String getAddress() {
    return address;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  // SETTERS
  public void setFirstName(String firstName) {
    this.firstName = firstName;
    this.updatedAt = LocalDateTime.now(); // Actualiza la fecha de modificación al cambiar el nombre
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
    this.updatedAt = LocalDateTime.now(); // Actualiza la fecha de modificación al cambiar el apellido
  } 

  public void setEmail(String email) {
    this.email = email;
    this.updatedAt = LocalDateTime.now(); // Actualiza la fecha de modificación al cambiar el email
  } 

  public void setPhone(String phone) {
    this.phone = phone;
    this.updatedAt = LocalDateTime.now(); // Actualiza la fecha de modificación al cambiar el teléfono
  }

  public void setAddress(String address) {
    this.address = address;
    this.updatedAt = LocalDateTime.now(); // Actualiza la fecha de modificación al cambiar la dirección
  }
}