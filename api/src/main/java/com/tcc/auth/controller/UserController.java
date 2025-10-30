package com.tcc.auth.controller;

import com.tcc.auth.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getUserStatus(
        @AuthenticationPrincipal CustomUserDetails principal) {
        
        if (principal == null) {
            return ResponseEntity.status(401).build(); // Não autenticado
        }

        if (principal.isPendingRegistration()) {
            Map<String, Object> response = Map.of(
                "status", "PENDING_REGISTRATION",
                "email", principal.getEmail(),
                "nome", principal.getNome()
            );
            return ResponseEntity.ok(response);
        
        } else {
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
            @AuthenticationPrincipal CustomUserDetails principal, 
            @Valid @RequestBody ProfileRequest request) {
            
        if (principal == null) {
             return ResponseEntity.status(401).build();
        }

        String email = principal.getEmail();

        InvestorProfile perfilEnum;
        try {
            perfilEnum = InvestorProfile.valueOf(request.getPerfil().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }

        User savedUser = userService.saveOrUpdateByEmail(email, request.getNome(), perfilEnum);

        CustomUserDetails newPrincipal = new CustomUserDetails(savedUser, principal.getAttributes());

        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        UsernamePasswordAuthenticationToken newAuth =
            new UsernamePasswordAuthenticationToken(newPrincipal, null, newPrincipal.getAuthorities());
        
        if (currentAuth != null) {
            newAuth.setDetails(currentAuth.getDetails());
        }
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        ProfileResponse resp = new ProfileResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getNome(), savedUser.getPerfil());
        return ResponseEntity.ok(resp);
    }
}
