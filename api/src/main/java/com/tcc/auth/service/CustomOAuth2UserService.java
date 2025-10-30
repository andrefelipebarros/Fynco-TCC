package com.tcc.auth.service;

import com.tcc.auth.model.user.User;
import com.tcc.auth.repository.UserRepository;
import com.tcc.auth.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Pega os dados do Google
        OAuth2User oauth2User = super.loadUser(userRequest);
        String email = oauth2User.getAttribute("email");

        // 2. Procura no banco
        Optional<User> userOptional = userRepository.findByEmail(email);

        // 3. Cria o Principal
        return userOptional
                .map(user -> new CustomUserDetails(user, oauth2User.getAttributes())) // Usuário existe
                .orElseGet(() -> new CustomUserDetails(null, oauth2User.getAttributes())); // Usuário é novo (null)
    }
}