package com.example.customer_management_app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Customer Management API",
        version = "v1",
        description = "API para gestionar clientes: creación, consulta, actualización y eliminación."
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Ambiente local")
    }
)
public class OpenApiConfig {
    // Configuración global de documentación OpenAPI/Swagger
}
