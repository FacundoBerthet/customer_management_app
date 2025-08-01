package com.example.customer_management_app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerManagementAPP {

  private static final Logger log = LoggerFactory.getLogger(CustomerManagementAPP.class);

  public static void main(String[] args) {
    SpringApplication.run(CustomerManagementAPP.class);
  }

  @Bean
  public CommandLineRunner demo(CustomerRepository repository) {
    return (args) -> {
      // Usando constructor con 3 parámetros (nombre, apellido, email)
      repository.save(new Customer("Jack", "Bauer", "jack.bauer@ctu.gov"));
      repository.save(new Customer("Chloe", "O'Brian", "chloe.obrian@ctu.gov"));
      repository.save(new Customer("Kim", "Bauer", "kim.bauer@gmail.com"));
      repository.save(new Customer("David", "Palmer", "david.palmer@whitehouse.gov"));
      repository.save(new Customer("Michelle", "Dessler", "michelle.dessler@ctu.gov"));

      // Usando constructor con 5 parámetros (todos los campos)
      repository.save(new Customer("Tony", "Almeida", "tony.almeida@ctu.gov", "555-1234", "Los Angeles, CA"));
      repository.save(new Customer("Edgar", "Stiles", "edgar.stiles@ctu.gov", "555-5678", "Beverly Hills, CA"));

      // fetch all customers
      log.info("Customers found with findAll():");
      log.info("-------------------------------");
      repository.findAll().forEach(customer -> {
        log.info(customer.toString());
      });
      log.info("");

      // fetch an individual customer by ID
      Customer customer = repository.findById(1L);
      log.info("Customer found with findById(1L):");
      log.info("--------------------------------");
      log.info(customer.toString());
      log.info("");

      // fetch customers by last name
      log.info("Customer found with findByLastName('Bauer'):");
      log.info("--------------------------------------------");
      repository.findByLastName("Bauer").forEach(bauer -> {
        log.info(bauer.toString());
      });
      log.info("");
    };
  }
}