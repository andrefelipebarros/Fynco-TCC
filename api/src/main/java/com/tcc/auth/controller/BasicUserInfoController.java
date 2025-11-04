package com.tcc.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.auth.model.user.dto.BasicUserInfoResponse;
import com.tcc.auth.service.UserService;

@RestController
@RequestMapping("/api/user")
public class BasicUserInfoController {

    private final UserService userService;

    public BasicUserInfoController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/basic")
    public ResponseEntity<BasicUserInfoResponse> getBasicUserInfo(
            @AuthenticationPrincipal OAuth2User oauthUser) {

        if (oauthUser == null) {
            return ResponseEntity.status(401).build();
        }

        String email = oauthUser.getAttribute("email");
        var user = userService.getUserStatusByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // pega nome e perfil do seu user salvo no banco
        String name = user.getNome();
        String profile = user.getPerfil() != null ? user.getPerfil().name() : null;

        return ResponseEntity.ok(new BasicUserInfoResponse(name, profile));
    }
}

