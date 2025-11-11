package com.infosaudepro.exemplo.model;

import jakarta.persistence.*;

// Lombok @Data removido
@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cpfCriptografado;
    private String diagnosticoCriptografado;

    // --- GETTERS E SETTERS ADICIONADOS MANUALMENTE ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Necessário para: paciente.setNome(dto.getNome());
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // Necessário para: paciente.setCpfCriptografado(...) e paciente.getCpfCriptografado()
    public String getCpfCriptografado() {
        return cpfCriptografado;
    }

    public void setCpfCriptografado(String cpfCriptografado) {
        this.cpfCriptografado = cpfCriptografado;
    }

    // Necessário para: paciente.setDiagnosticoCriptografado(...) e paciente.getDiagnosticoCriptografado()
    public String getDiagnosticoCriptografado() {
        return diagnosticoCriptografado;
    }

    public void setDiagnosticoCriptografado(String diagnosticoCriptografado) {
        this.diagnosticoCriptografado = diagnosticoCriptografado;
    }
}