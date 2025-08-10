package com.example.customer_management_app.config;

import org.springframework.context.annotation.Bean; // Declaro beans de configuración
import org.springframework.context.annotation.Configuration; // Clase de configuración Spring
import org.springframework.context.annotation.Profile; // Activo solo en un perfil específico
import org.springframework.web.servlet.config.annotation.CorsRegistry; // Configuro CORS por rutas
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // Hook para personalizar MVC

/**
 * Configuración de CORS SOLO para el perfil "dev".
 * 
 * ¿Qué es CORS?
 * - Es un mecanismo del navegador que restringe peticiones entre orígenes distintos.
 * - Cuando mi front (http://localhost:5173 o 3000) llama a mi API (http://localhost:8080),
 *   el navegador verifica si la API permite ese origen.
 * - Acá declaro que SÍ permito esos orígenes en desarrollo.
 */
@Configuration // Esta clase define configuración para Spring
@Profile("dev") // SOLO se carga cuando el perfil activo es "dev"
public class DevCorsConfig {

    /**
     * Defino un WebMvcConfigurer para registrar reglas CORS.
     * Se aplica a todas las rutas (/**) y a los métodos básicos REST.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Mapeo TODAS las rutas de la API
                registry.addMapping("/**")
                    // Orígenes permitidos en desarrollo: Vite (5173) y CRA (3000)
                    .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                    // Métodos HTTP permitidos
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    // Headers permitidos (útil si después agrego auth)
                    .allowedHeaders("Content-Type", "Authorization")
                    // Permito credenciales (cookies/autenticación basada en sesión)
                    .allowCredentials(true);
            }
        };
    }
}
