// src/main/java/com/infosaudepro/exemplo/service/PacienteService.java
package com.infosaudepro.exemplo.service;

import com.infosaudepro.exemplo.dto.PacienteCadastroDTO; // Assumido
import com.infosaudepro.exemplo.exceptions.PacienteNaoEncontradoException;
import com.infosaudepro.exemplo.model.Paciente; // Assumido
import com.infosaudepro.exemplo.repository.PacienteRepository; // Assumido
import com.infosaudepro.exemplo.util.CriptografiaUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository repository;

    // Métodos de CRUD com criptografia/descriptografia (Cadastrar, Buscar, Atualizar)

    public Paciente cadastrarSeguro(PacienteCadastroDTO dto) {
        try {
            Paciente paciente = new Paciente();
            paciente.setNome(dto.getNome());

            // 🔒 CRIPTOGRAFIA DE DADOS EM REPOUSO
            paciente.setCpfCriptografado(CriptografiaUtil.encrypt(dto.getCpf()));
            paciente.setDiagnosticoCriptografado(CriptografiaUtil.encrypt(dto.getDiagnostico()));

            return repository.save(paciente);
        } catch (Exception e) {
            throw new RuntimeException("Falha de segurança ao salvar paciente (erro de criptografia).", e);
        }
    }

    public Paciente buscarDescriptografadoPorId(Long id) {
        Paciente paciente = repository.findById(id)
                // Lança a exceção 404
                .orElseThrow(() -> new PacienteNaoEncontradoException("Paciente não encontrado. ID: " + id));

        try {
            // 🔓 DESCRIPTOGRAFIA JUST-IN-TIME
            String cpfDescriptografado = CriptografiaUtil.decrypt(paciente.getCpfCriptografado());
            String diagnosticoDescriptografado = CriptografiaUtil.decrypt(paciente.getDiagnosticoCriptografado());

            // Retorna o objeto com dados sensíveis descriptografados
            paciente.setCpfCriptografado(cpfDescriptografado);
            paciente.setDiagnosticoCriptografado(diagnosticoDescriptografado);

            return paciente;
        } catch (Exception e) {
            throw new RuntimeException("Falha na descriptografia dos dados.", e);
        }
    }

    public Paciente atualizarSeguro(Long id, PacienteCadastroDTO dto) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new PacienteNaoEncontradoException("Paciente não encontrado para atualização. ID: " + id));

        try {
            paciente.setNome(dto.getNome());

            // Criptografa e atualiza CPF e Diagnóstico
            paciente.setCpfCriptografado(CriptografiaUtil.encrypt(dto.getCpf()));
            paciente.setDiagnosticoCriptografado(CriptografiaUtil.encrypt(dto.getDiagnostico()));

            return repository.save(paciente);
        } catch (Exception e) {
            throw new RuntimeException("Falha de segurança ao atualizar paciente (erro de criptografia).", e);
        }
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new PacienteNaoEncontradoException("Paciente não encontrado para exclusão. ID: " + id);
        }
        repository.deleteById(id);
    }
}