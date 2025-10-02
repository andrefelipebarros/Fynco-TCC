package com.tcc.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

    @PostMapping("/profile")
    public ResponseEntity<ProfileResponse> saveOrUpdateProfile(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @Valid @RequestBody ProfileRequest request) {

        String email = oauth2User.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        // normaliza e converte para o enum
        InvestorProfile perfilEnum;
        try {
            perfilEnum = InvestorProfile.valueOf(request.getPerfil().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }

        User updated = userService.saveOrUpdateByEmail(email, request.getNome(), perfilEnum);

        ProfileResponse resp = new ProfileResponse(updated.getId(), updated.getEmail(), updated.getNome(), updated.getPerfil());
        return ResponseEntity.ok(resp);
    }
}
