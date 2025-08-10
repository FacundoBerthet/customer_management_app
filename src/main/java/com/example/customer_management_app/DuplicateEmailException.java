package com.example.customer_management_app;

/**
 * Excepción de negocio para indicar que un email ya existe.
 */
public class DuplicateEmailException extends RuntimeException {
  public DuplicateEmailException(String message) {
    super(message);
  }
}
