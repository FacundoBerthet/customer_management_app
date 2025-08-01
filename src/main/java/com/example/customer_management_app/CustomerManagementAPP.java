package com.example.customer_management_app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
// Importaciones para validación manual
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validation;
import java.util.Set;

@SpringBootApplication
public class CustomerManagementAPP {

  private static final Logger log = LoggerFactory.getLogger(CustomerManagementAPP.class);

  public static void main(String[] args) {
    SpringApplication.run(CustomerManagementAPP.class);
  }

  @Bean
  public CommandLineRunner demo(CustomerRepository repository) {
    return (args) -> {
      // ⭐ CONFIGURAR VALIDADOR MANUAL
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      Validator validator = factory.getValidator();

      // ✅ DATOS VÁLIDOS - estos deberían funcionar
      log.info("🟢 PROBANDO DATOS VÁLIDOS:");
      log.info("==============================");
      
      Customer validCustomer = new Customer("Jack", "Bauer", "jack.bauer@ctu.gov");
      Set<ConstraintViolation<Customer>> violations = validator.validate(validCustomer);
      
      if (violations.isEmpty()) {
        repository.save(validCustomer);
        log.info("✅ Cliente válido guardado: " + validCustomer.toString());
      } else {
        log.error("❌ Cliente inválido, errores:");
        violations.forEach(v -> log.error("   - " + v.getMessage()));
      }

      // ❌ DATOS INVÁLIDOS - estos deberían fallar las validaciones
      log.info("");
      log.info("🔴 PROBANDO DATOS INVÁLIDOS:");
      log.info("==============================");

      // Test 1: Nombre muy corto
      Customer shortName = new Customer("A", "Bauer", "test@email.com");
      violations = validator.validate(shortName);
      log.info("🧪 Test 1 - Nombre muy corto:");
      if (!violations.isEmpty()) {
        violations.forEach(v -> log.error("   ❌ " + v.getMessage()));
      }

      // Test 2: Email inválido
      Customer invalidEmail = new Customer("Juan", "Pérez", "email-malformado");
      violations = validator.validate(invalidEmail);
      log.info("🧪 Test 2 - Email inválido:");
      if (!violations.isEmpty()) {
        violations.forEach(v -> log.error("   ❌ " + v.getMessage()));
      }

      // Test 3: Teléfono con formato incorrecto
      Customer invalidPhone = new Customer("María", "García", "maria@email.com", "123456789", "Dirección");
      violations = validator.validate(invalidPhone);
      log.info("🧪 Test 3 - Teléfono formato incorrecto:");
      if (!violations.isEmpty()) {
        violations.forEach(v -> log.error("   ❌ " + v.getMessage()));
      }

      // Test 4: Nombre vacío
      Customer emptyName = new Customer("", "Apellido", "test@email.com");
      violations = validator.validate(emptyName);
      log.info("🧪 Test 4 - Nombre vacío:");
      if (!violations.isEmpty()) {
        violations.forEach(v -> log.error("   ❌ " + v.getMessage()));
      }

      // ✅ GUARDAR ALGUNOS DATOS VÁLIDOS PARA LAS CONSULTAS
      log.info("");
      log.info("💾 GUARDANDO DATOS VÁLIDOS PARA PRUEBAS:");
      log.info("=========================================");
      
      repository.save(new Customer("Chloe", "O'Brian", "chloe.obrian@ctu.gov"));
      repository.save(new Customer("Kim", "Bauer", "kim.bauer@gmail.com"));
      repository.save(new Customer("Tony", "Almeida", "tony.almeida@ctu.gov", "555-1234", "Los Angeles, CA"));

      // 📋 MOSTRAR TODOS LOS CLIENTES GUARDADOS
      log.info("");
      log.info("📋 CLIENTES GUARDADOS EN LA BASE DE DATOS:");
      log.info("==========================================");
      repository.findAll().forEach(customer -> {
        log.info("✅ " + customer.toString());
      });

      factory.close();
    };
  }
}