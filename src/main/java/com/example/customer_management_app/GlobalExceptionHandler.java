package com.example.customer_management_app;

import java.time.OffsetDateTime; // Fecha y hora con zona

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler; // Manejar excepciones específicas
import org.springframework.web.bind.annotation.RestControllerAdvice; // Atajo para @ControllerAdvice + @ResponseBody
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler; // Base para manejar validaciones

import jakarta.servlet.http.HttpServletRequest;

/**
 * Manejador global de excepciones para respuestas de error consistentes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  // 400 - Validación de Bean Validation (@Valid)
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {

    String firstError = ex.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(FieldError::getDefaultMessage)
        .orElse("Validation error");

    ErrorResponse body = new ErrorResponse(
        OffsetDateTime.now(),
        request.getDescription(false).replace("uri=", ""),
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        firstError
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  // 404 - Recurso no encontrado (usamos IllegalArgumentException lanzada en Service para simplificar)
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
    ErrorResponse body = new ErrorResponse(
        OffsetDateTime.now(),
        req.getRequestURI(),
        HttpStatus.NOT_FOUND.value(),
        HttpStatus.NOT_FOUND.getReasonPhrase(),
        ex.getMessage() != null ? ex.getMessage() : "Resource not found"
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  // 409 - Conflicto (ej. email duplicado)
  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex, HttpServletRequest req) {
    ErrorResponse body = new ErrorResponse(
        OffsetDateTime.now(),
        req.getRequestURI(),
        HttpStatus.CONFLICT.value(),
        HttpStatus.CONFLICT.getReasonPhrase(),
        ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }

  // 500 - Cualquier otro error no manejado
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
    ErrorResponse body = new ErrorResponse(
        OffsetDateTime.now(),
        req.getRequestURI(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
        "Unexpected error"
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
