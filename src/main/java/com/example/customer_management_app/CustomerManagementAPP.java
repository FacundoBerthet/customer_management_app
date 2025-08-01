package com.example.customer_management_app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.Optional;

@SpringBootApplication
public class CustomerManagementAPP {

  private static final Logger log = LoggerFactory.getLogger(CustomerManagementAPP.class);

  public static void main(String[] args) {
    SpringApplication.run(CustomerManagementAPP.class);
  }

  @Bean
  public CommandLineRunner demo(CustomerRepository repository) {
    return (args) -> {
      
      // 💾 GUARDAR DATOS DE PRUEBA
      log.info("� GUARDANDO DATOS DE PRUEBA:");
      log.info("============================");
      
      repository.save(new Customer("Jack", "Bauer", "jack.bauer@ctu.gov", "555-0001", "Los Angeles"));
      repository.save(new Customer("Chloe", "O'Brian", "chloe.obrian@ctu.gov", "555-0002", "CTU Headquarters"));
      repository.save(new Customer("Kim", "Bauer", "kim.bauer@gmail.com")); // Sin teléfono ni dirección
      repository.save(new Customer("Tony", "Almeida", "tony.almeida@ctu.gov", "555-0003", "Los Angeles"));
      repository.save(new Customer("Michelle", "Dessler", "michelle.dessler@yahoo.com")); // Sin teléfono ni dirección
      repository.save(new Customer("David", "Palmer", "david.palmer@whitehouse.gov", "555-0004", "Washington DC"));
      repository.save(new Customer("Edgar", "Stiles", "edgar.stiles@ctu.gov"));
      repository.save(new Customer("Charles", "Logan", "charles.logan@government.gov"));
      repository.save(new Customer("Bill", "Buchanan", "bill.buchanan@ctu.gov", "555-0005", "CTU"));
      
      log.info("✅ {} clientes guardados correctamente!", repository.count());
      log.info("");

      // 🔍 PROBAR QUERIES AUTOMÁTICAS BÁSICAS
      log.info("� PROBANDO QUERIES AUTOMÁTICAS BÁSICAS:");
      log.info("=========================================");
      
      // Buscar por apellido
      log.info("👥 Clientes con apellido 'Bauer':");
      repository.findByLastName("Bauer").forEach(customer -> 
        log.info("   ✅ " + customer.toString()));
      
      // Buscar por nombre
      log.info("👤 Clientes con nombre 'Jack':");
      repository.findByFirstName("Jack").forEach(customer -> 
        log.info("   ✅ " + customer.toString()));
      
      // Buscar por email (usando Optional)
      log.info("📧 Cliente con email 'kim.bauer@gmail.com':");
      Optional<Customer> customerOpt = repository.findByEmail("kim.bauer@gmail.com");
      if (customerOpt.isPresent()) {
        log.info("   ✅ Encontrado: " + customerOpt.get().toString());
      } else {
        log.info("   ❌ No encontrado");
      }
      
      log.info("");

      // 🔎 PROBAR BÚSQUEDAS AVANZADAS
      log.info("🔎 PROBANDO BÚSQUEDAS AVANZADAS:");
      log.info("================================");
      
      // Buscar que contenga texto
      log.info("🔍 Clientes cuyo nombre contenga 'Ch':");
      repository.findByFirstNameContaining("Ch").forEach(customer -> 
        log.info("   ✅ " + customer.toString()));
      
      // Buscar por apellido que contenga texto
      log.info("🔍 Clientes cuyo apellido contenga 'Ba':");
      repository.findByLastNameContaining("Ba").forEach(customer -> 
        log.info("   ✅ " + customer.toString()));
      
      // Buscar ignorando mayúsculas/minúsculas
      log.info("🔤 Clientes con nombre 'jack' (ignore case):");
      repository.findByFirstNameIgnoreCase("jack").forEach(customer -> 
        log.info("   ✅ " + customer.toString()));
      
      // Buscar por múltiples criterios
      log.info("🎯 Clientes que contengan 'Ch' en nombre Y 'O' en apellido:");
      repository.findByFirstNameContainingAndLastNameContaining("Ch", "O").forEach(customer -> 
        log.info("   ✅ " + customer.toString()));
      
      log.info("");

      // ✅ PROBAR VERIFICACIONES Y CONTADORES
      log.info("✅ PROBANDO VERIFICACIONES Y CONTADORES:");
      log.info("========================================");
      
      // Verificar si existe email
      boolean exists1 = repository.existsByEmail("jack.bauer@ctu.gov");
      log.info("❓ ¿Existe email 'jack.bauer@ctu.gov'? " + (exists1 ? "✅ SÍ" : "❌ NO"));
      
      boolean exists2 = repository.existsByEmail("noexiste@email.com");
      log.info("❓ ¿Existe email 'noexiste@email.com'? " + (exists2 ? "✅ SÍ" : "❌ NO"));
      
      // Contar por apellido
      long bauerCount = repository.countByLastName("Bauer");
      log.info("🔢 Cantidad de clientes con apellido 'Bauer': " + bauerCount);
      
      long palmerCount = repository.countByLastName("Palmer");
      log.info("🔢 Cantidad de clientes con apellido 'Palmer': " + palmerCount);
      
      log.info("");

      // 🏢 PROBAR QUERIES NATIVAS
      log.info("🏢 PROBANDO QUERIES NATIVAS:");
      log.info("============================");
      
      // Contar por dominio de email usando query nativa
      long ctuCount = repository.countByEmailDomainNative("ctu.gov");
      log.info("🏢 Cantidad de clientes con email de CTU (.ctu.gov): " + ctuCount);
      
      long gmailCount = repository.countByEmailDomainNative("gmail.com");
      log.info("📧 Cantidad de clientes con Gmail (.gmail.com): " + gmailCount);
      
      long govCount = repository.countByEmailDomainNative("gov");
      log.info("🏛️ Cantidad de clientes con email .gov: " + govCount);
      
      log.info("");

      // � MOSTRAR TODOS ORDENADOS POR FECHA DE CREACIÓN
      log.info("� TODOS LOS CLIENTES (ordenados por fecha de creación - más recientes primero):");
      log.info("===============================================================================");
      repository.findAllOrderByCreatedAtDesc().forEach(customer -> 
        log.info("✅ " + customer.toString()));
      
      log.info("");
      log.info("🎉 ¡TODAS LAS QUERIES FUNCIONARON CORRECTAMENTE!");
      log.info("===============================================");
    };
  }
}