package com.tcc.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tcc.auth.model.user.InvestorProfile;
import com.tcc.auth.model.user.User;
import com.tcc.auth.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository usuarioRepository;

    public UserService(UserRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<User> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public User save(User user) {
        return usuarioRepository.save(user);
    }

    @Transactional
    public void completeQuestionnaire(String email, InvestorProfile profile) {
        Optional<User> optionalUser = usuarioRepository.findByEmail(email);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPerfil(profile);
            user.setCompletedQuestionnaire(true);
            usuarioRepository.save(user);
        }
    }
}
