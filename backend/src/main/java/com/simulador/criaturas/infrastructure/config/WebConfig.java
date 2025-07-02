package com.simulador.criaturas.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Permite CORS para todos os seus endpoints
                .allowedOrigins("http://localhost:3000", "https://simulador-backend-ngs9.onrender.com/api")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos
                .allowedHeaders("*") // Permite todos os cabeçalhos
                .allowCredentials(true);
    }
}
