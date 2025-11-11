package com.infosaudepro.exemplo.controller; // PACOTE CORRETO

import com.infosaudepro.exemplo.model.Paciente;
import com.infosaudepro.exemplo.service.PacienteService; // ⬅️ CORRIGIDO: Importa a CLASSE
import com.infosaudepro.exemplo.dto.PacienteCadastroDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Necessário para a segurança de roles
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/pacientes") // Endpoint principal
public class PacienteController {

    @Autowired
    private PacienteService service;

    /**
     * Endpoint de Cadastro (POST).
     * 🔒 Demonstra Autorização: Requer ROLE_ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Paciente> cadastrar(@RequestBody PacienteCadastroDTO dto) {
        // O Service aplica a Criptografia AES antes de salvar
        Paciente novoPaciente = service.cadastrarSeguro(dto);
        return ResponseEntity.ok(novoPaciente);
    }

    /**
     * Endpoint de Consulta por ID (GET).
     * 🔒 Demonstra Autorização: Requer ROLE_ADMIN ou ROLE_MEDICO.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) {
        // O Service descriptografa o dado do banco ANTES de retornar
        Paciente pacienteDescriptografado = service.buscarDescriptografadoPorId(id);
        return ResponseEntity.ok(pacienteDescriptografado);
    }
}