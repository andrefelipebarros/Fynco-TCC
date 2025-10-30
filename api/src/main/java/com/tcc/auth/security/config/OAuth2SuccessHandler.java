package com.tcc.auth.security.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.tcc.auth.model.user.User;
import com.tcc.auth.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public OAuth2SuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        Optional<User> existing = userService.findByEmail(email);
        if (existing.isEmpty()) {
            // Cria usuário sem nome e perfil inicialmente (usando construtor padrão + setters)
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setCompletedQuestionnaire(false);
            userService.save(newUser);
        }

        response.sendRedirect("/questionnaire"); // redireciona para questionário
    }
}
