package com.tcc.auth.controller;

import com.tcc.auth.model.user.InvestorProfile;
import com.tcc.auth.service.UserService;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questionnaire")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/submit")
    public String submitProfile(@AuthenticationPrincipal OAuth2User oauthUser,
                                @RequestParam String profile) {

        String email = oauthUser.getAttribute("email");

        InvestorProfile investorProfile;
        try {
            investorProfile = InvestorProfile.valueOf(profile.toUpperCase());
        } catch (IllegalArgumentException e) {
            return "Perfil inválido! Use: CONSERVATOR, MODERATE ou AGGRESSIVE";
        }

        userService.completeQuestionnaire(email, investorProfile);
        return "Perfil salvo com sucesso!";
    }
}
