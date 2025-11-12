// src/main/java/com/infosaudepro/exemplo/config/SecurityConfig.java
package com.infosaudepro.exemplo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🚨 Habilita CORS, usando o bean definido abaixo
                .cors(withDefaults())

                // Desabilita CSRF (comum para APIs REST)
                .csrf(AbstractHttpConfigurer::disable)

                // Habilita autenticação HTTP Basic
                .httpBasic(withDefaults())

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/login").authenticated()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * Bean de configuração CORS para permitir acesso do frontend (porta 8000).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🔑 Permite acesso do seu frontend
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8000"));

        // Permite os métodos de CRUD
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Permite headers necessários para Basic Auth e JSON
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Permite o envio de credenciais (Basic Auth)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}