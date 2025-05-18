package com.dmitry.books.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Путь, для которого разрешаем CORS
                        .allowedOrigins("http://localhost:3000") // Разрешённый источник (фронтенд)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Разрешённые методы
                        .allowedHeaders("*") // Разрешённые заголовки
                        .exposedHeaders("Authorization") // Заголовки, которые будут доступны клиенту
                        .allowCredentials(true); // Разрешить отправку кук/credentials
            }
        };
    }
}