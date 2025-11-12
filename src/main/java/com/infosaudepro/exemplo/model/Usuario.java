// src/main/java/com/infosaudepro/exemplo/model/Usuario.java
package com.infosaudepro.exemplo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "Usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario implements UserDetails {

    @Id
    private String username;
    private String password;
    private String role;

    // 🔑 CONSTRUTOR NECESSÁRIO PARA O DATALOADER 🔑
    public Usuario(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Implementação dos métodos de UserDetails
    @Override
    public Collection<? extends SimpleGrantedAuthority> getAuthorities() {
        // Mapeia a string 'role' para o formato exigido pelo Spring Security 'ROLE_ADMIN'
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}