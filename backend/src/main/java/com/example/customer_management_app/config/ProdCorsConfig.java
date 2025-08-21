package com.example.customer_management_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de CORS para PRODUCCIÓN.
 * Lee la propiedad app.cors.allowed-origins (separada por comas) y aplica las reglas.
 * Mantiene la misma política que en dev pero limitada a los orígenes explícitos.
 */
@Configuration
@Profile("prod")
public class ProdCorsConfig {

    @Value("${app.cors.allowed-origins:https://facundoberthet.github.io}")
    private String allowedOriginsProp;

    @Bean
    public WebMvcConfigurer prodCorsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String[] origins = StringUtils.commaDelimitedListToStringArray(allowedOriginsProp);
                registry.addMapping("/**")
                        .allowedOrigins(origins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Content-Type", "Authorization")
                        .allowCredentials(true);
            }
        };
    }
}
