package com.tcc.auth.security;

import com.tcc.auth.model.user.InvestorProfile;
import com.tcc.auth.model.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements OAuth2User {

    private final User user;

    private final Map<String, Object> attributes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user != null && user.getPerfil() != null) {
            // Usuário completo: retorna o perfil real como autoridade
            // Ex: "CONSERVADOR" ou "ROLE_CONSERVADOR" (ajuste conforme sua necessidade)
            return List.of(new SimpleGrantedAuthority(user.getPerfil().toString()));
        }
        if (user == null) {
            // Usuário pendente: retorna uma role temporária
            return List.of(new SimpleGrantedAuthority("ROLE_PENDING_REGISTRATION"));
        }
        // Caso de usuário existir mas não ter perfil (não deve acontecer)
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        // O 'name' do Principal será o email
        return this.getEmail();
    }

    // --- Métodos Helper (Delegam para 'user' ou 'attributes') ---

    public UUID getId() {
        return (user != null) ? user.getId() : null;
    }

    public String getEmail() {
        // Se o usuário existe, pega do banco (fonte da verdade)
        // Se não, pega dos atributos do Google
        return (user != null) ? user.getEmail() : (String) attributes.get("email");
    }

    public String getNome() {
        // Se o usuário existe, pega do banco
        // Se não, pega dos atributos do Google
        return (user != null) ? user.getNome() : (String) attributes.get("name");
    }

    public InvestorProfile getPerfil() {
        return (user != null) ? user.getPerfil() : null;
    }

    /**
     * Helper para verificar se o cadastro está pendente.
     * @return true se o usuário ainda não foi salvo no banco.
     */
    public boolean isPendingRegistration() {
        return this.user == null;
    }
}