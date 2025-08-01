// Define el paquete donde se encuentra esta clase - organiza el código en estructura jerárquica
package com.example.customer_management_app;

// Importa la anotación @Entity para marcar esta clase como entidad JPA
import jakarta.persistence.Entity;
// Importa la anotación @GeneratedValue para generar valores automáticamente
import jakarta.persistence.GeneratedValue;
// Importa el enum GenerationType para especificar la estrategia de generación de IDs
import jakarta.persistence.GenerationType;
// Importa la anotación @Id para marcar el campo como clave primaria
import jakarta.persistence.Id;

// Marca esta clase como una entidad JPA - se mapeará a una tabla en la base de datos
@Entity
public class Customer {

  // Marca este campo como la clave primaria de la tabla
  @Id
  // Configura la generación automática del ID - JPA elegirá la mejor estrategia
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id; // Campo para almacenar el ID único del cliente (clave primaria)
  private String firstName; // Campo para almacenar el nombre del cliente
  private String lastName;  // Campo para almacenar el apellido del cliente

  // Constructor vacío protegido - requerido por JPA para crear instancias de la entidad
  protected Customer() {}

  // Constructor público para crear nuevos objetos Customer con nombre y apellido
  public Customer(String firstName, String lastName) {
    this.firstName = firstName; // Asigna el nombre recibido al campo firstName
    this.lastName = lastName;   // Asigna el apellido recibido al campo lastName
  }

  // Sobrescribe el método toString() para representar el objeto como String legible
  @Override
  public String toString() {
    // Retorna una cadena formateada con los datos del cliente
    return String.format(
        "Customer[id=%d, firstName='%s', lastName='%s']",
        id, firstName, lastName);
  }

  // Método getter para obtener el ID del cliente
  public Long getId() {
    return id; // Retorna el valor del campo id
  }

  // Método getter para obtener el nombre del cliente
  public String getFirstName() {
    return firstName; // Retorna el valor del campo firstName
  }

  // Método getter para obtener el apellido del cliente
  public String getLastName() {
    return lastName; // Retorna el valor del campo lastName
  }
}