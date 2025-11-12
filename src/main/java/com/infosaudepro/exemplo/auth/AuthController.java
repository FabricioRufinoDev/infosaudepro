package com.infosaudepro.exemplo.auth; // ⬅️ DEVE SER ESTE O PACOTE CORRETO

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // PONTO DE SEGURANÇA: Esta rota é pública, mas é protegida por HTTP Basic
    @PostMapping("/login")
    public ResponseEntity<?> loginSuccess() {
        // Se o Spring Security autenticar com sucesso, a requisição chega aqui.
        return ResponseEntity.ok("Autenticação bem-sucedida. Implementação JWT recomendada aqui.");
    }
}