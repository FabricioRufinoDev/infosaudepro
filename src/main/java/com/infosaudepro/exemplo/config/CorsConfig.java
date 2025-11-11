package com.infosaudepro.exemplo.config; // Seu pacote

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; // <-- ESTA LINHA É A SOLUÇÃO!
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // ✅ Permite acesso de localhost:8000 e 127.0.0.1:8000
                registry.addMapping("/**") // Aplica a configuração a TODOS os endpoints do backend
                        .allowedOrigins(
                                "http://localhost:8000",
                                "http://127.0.0.1:8000" // ⬅️ CORRIGIDO: Removido o caminho ("/front-end/")
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*") // Permite todos os cabeçalhos
                        .allowCredentials(true); // Permite cookies, headers de autorização, etc.
            }
        };
    }
}