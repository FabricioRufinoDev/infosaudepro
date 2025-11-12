// src/main/java/com/infosaudepro/exemplo/controller/PacienteController.java
package com.infosaudepro.exemplo.controller;

import com.infosaudepro.exemplo.dto.PacienteCadastroDTO; // Assumido
import com.infosaudepro.exemplo.model.Paciente; // Assumido
import com.infosaudepro.exemplo.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService service;

    /**
     * CREATE: Apenas ADMIN pode cadastrar.
     * URL: POST /api/pacientes/cadastrarSeguro
     */
    @PostMapping("/cadastrarSeguro")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Paciente cadastrar(@RequestBody PacienteCadastroDTO dto) {
        return service.cadastrarSeguro(dto);
    }

    /**
     * READ: ADMIN ou MEDICO podem consultar.
     * URL: GET /api/pacientes/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public Paciente buscarPorId(@PathVariable Long id) {
        return service.buscarDescriptografadoPorId(id);
    }

    /**
     * UPDATE: Apenas ADMIN pode atualizar.
     * URL: PUT /api/pacientes/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Paciente atualizar(@PathVariable Long id, @RequestBody PacienteCadastroDTO dto) {
        return service.atualizarSeguro(id, dto);
    }

    /**
     * DELETE: Apenas ADMIN pode excluir.
     * URL: DELETE /api/pacientes/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}