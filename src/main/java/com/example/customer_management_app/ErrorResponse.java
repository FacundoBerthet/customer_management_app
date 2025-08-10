package com.example.customer_management_app;

import java.time.OffsetDateTime; // Manejar fecha y hora con zona

import io.swagger.v3.oas.annotations.media.Schema; // Documentación Swagger/OpenAPI

/**
 * Modelo consistente para respuestas de error de la API.
 */
@Schema(name = "ErrorResponse", description = "Standard error response returned by the API")
public class ErrorResponse {

  // Timestamp del error (en formato ISO-8601)
  @Schema(description = "Error timestamp (ISO-8601)", example = "2025-08-10T12:34:56.789Z")
  private OffsetDateTime timestamp;

  // Ruta del endpoint que produjo el error
  @Schema(description = "Request path where the error occurred", example = "/api/customers/1")
  private String path;

  // Código de estado HTTP
  @Schema(description = "HTTP status code", example = "404")
  private int status;

  // Nombre del error (razón del estado)
  @Schema(description = "HTTP error reason", example = "Not Found")
  private String error;

  // Mensaje legible del error
  @Schema(description = "Human-readable error message", example = "Customer not found")
  private String message;

  // Constructor completo
  public ErrorResponse(OffsetDateTime timestamp, String path, int status, String error, String message) {
    this.timestamp = timestamp;
    this.path = path;
    this.status = status;
    this.error = error;
    this.message = message;
  }

  // Getters (no setters para mantener inmutabilidad desde afuera)
  public OffsetDateTime getTimestamp() { return timestamp; }
  public String getPath() { return path; }
  public int getStatus() { return status; }
  public String getError() { return error; }
  public String getMessage() { return message; }
}
