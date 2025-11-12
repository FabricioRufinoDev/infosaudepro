// src/main/java/com/infosaudepro/exemplo/exceptions/PacienteNaoEncontradoException.java
package com.infosaudepro.exemplo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção personalizada que, quando lançada, 
 * faz o Spring Boot retornar automaticamente o status HTTP 404 (Not Found).
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // <-- A chave para retornar HTTP 404
public class PacienteNaoEncontradoException extends RuntimeException {

    public PacienteNaoEncontradoException(String message) {
        super(message);
    }

    // Opcional: construtor com causa, para exceções encadeadas
    public PacienteNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}