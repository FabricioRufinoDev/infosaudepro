package com.infosaudepro.exemplo.dto;
import lombok.Data;

@Data
public class PacienteCadastroDTO {
    private String nome;
    private String cpf; // Em texto puro antes de ser criptografado
    private String diagnostico; // Em texto puro antes de ser criptografado

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }
}