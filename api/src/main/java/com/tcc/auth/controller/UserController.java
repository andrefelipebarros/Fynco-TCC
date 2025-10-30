package com.tcc.auth.controller;

// --- Imports Adicionais Necessários ---
import com.tcc.auth.security.CustomUserDetails; // 1. Importar seu Principal customizado
import org.springframework.security.core.context.SecurityContextHolder; // 2. Para promover a sessão
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken; // 3. Para promover a sessão
import org.springframework.web.bind.annotation.GetMapping; // 4. Para o endpoint /status
import java.util.Map; // 5. Para a resposta do /status
// --- Fim dos Imports Adicionais ---

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.auth.model.user.InvestorProfile;
import com.tcc.auth.model.user.User;
import com.tcc.auth.model.user.dto.ProfileRequest;
import com.tcc.auth.model.user.dto.ProfileResponse;
import com.tcc.auth.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getUserStatus(
        // 1. Recebe o CustomUserDetails, não o OAuth2User genérico
        @AuthenticationPrincipal CustomUserDetails principal) {
        
        if (principal == null) {
            return ResponseEntity.status(401).build(); // Não autenticado
        }

        if (principal.isPendingRegistration()) {
            // 2. Usuário é novo e precisa preencher o questionário
            Map<String, Object> response = Map.of(
                "status", "PENDING_REGISTRATION",
                "email", principal.getEmail(), // Pega dos attributes do Google
                "nome", principal.getNome()     // Pega dos attributes do Google
            );
            return ResponseEntity.ok(response);
        
        } else {
            // 3. Usuário já existe, retorna o perfil completo (assumindo que ProfileResponse aceita UUID)
            ProfileResponse resp = new ProfileResponse(
                principal.getId(), 
                principal.getEmail(), 
                principal.getNome(), 
                principal.getPerfil()
            );
            return ResponseEntity.ok(resp);
        }
    }

    @PostMapping("/profile")
    public ResponseEntity<ProfileResponse> saveOrUpdateProfile(
            // 1. Recebe o CustomUserDetails
            @AuthenticationPrincipal CustomUserDetails principal, 
            @Valid @RequestBody ProfileRequest request,
            // 2. Recebe o Token de Autenticação para podermos "promover" a sessão
            OAuth2AuthenticationToken authentication) {
            
        if (principal == null) {
             return ResponseEntity.status(401).build();
        }

        // 3. Pega o email do principal (que veio dos attributes do Google)
        String email = principal.getEmail();

        // normaliza e converte para o enum
        InvestorProfile perfilEnum;
        try {
            perfilEnum = InvestorProfile.valueOf(request.getPerfil().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }

        // 4. Salva o usuário no banco PELA PRIMEIRA VEZ
        // O nome vem do formulário (request), o email vem do Google (principal)
        User savedUser = userService.saveOrUpdateByEmail(email, request.getNome(), perfilEnum);

        // 5. --- PROMOÇÃO DA SESSÃO (PARTE CRUCIAL) ---
        // Agora que o usuário existe no banco, criamos um novo Principal com ele
        CustomUserDetails newPrincipal = new CustomUserDetails(savedUser, principal.getAttributes());
        
        // Criamos uma nova autenticação com o principal completo
        OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(
                newPrincipal,
                newPrincipal.getAuthorities(), // Agora terá as roles/perfis reais
                authentication.getAuthorizedClientRegistrationId() // Reusa o ID do provedor (ex: "google")
        );
        
        // Substituímos a autenticação "PENDENTE" pela "COMPLETA" na sessão atual
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        // --- FIM DA PROMOÇÃO ---

        // 6. Retorna a resposta com os dados do usuário recém-criado (incluindo o UUID)
        ProfileResponse resp = new ProfileResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getNome(), savedUser.getPerfil());
        return ResponseEntity.ok(resp);
    }
}