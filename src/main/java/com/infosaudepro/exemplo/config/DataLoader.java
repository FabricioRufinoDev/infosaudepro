// src/main/java/com/infosaudepro/exemplo/config/DataLoader.java

package com.infosaudepro.exemplo.config;

import com.infosaudepro.exemplo.model.Usuario; // Certifique-se de que esta classe existe
import com.infosaudepro.exemplo.repository.UsuarioRepository; // Certifique-se de que esta interface existe
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Se o banco de dados estiver vazio, insere os usuários
        if (repository.count() == 0) {

            String adminPassword = passwordEncoder.encode("admin123");
            String medicoPassword = passwordEncoder.encode("medico123");

            // Se a sua classe Usuario for diferente, ajuste o construtor aqui
            Usuario admin = new Usuario("admin", adminPassword, "ADMIN");
            Usuario medico = new Usuario("medico", medicoPassword, "MEDICO");

            repository.save(admin);
            repository.save(medico);

            System.out.println("✅ Usuários de segurança ADMIN e MEDICO inseridos via DataLoader.");
        } else {
            System.out.println("Usuários de segurança já existem no banco. DataLoader ignorado.");
        }
    }
}