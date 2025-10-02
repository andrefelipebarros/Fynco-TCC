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

    @Transactional
    public User saveOrUpdateByEmail(String email, String nome, InvestorProfile perfil) {
        Optional<User> opt = usuarioRepository.findByEmail(email);

        if (opt.isPresent()) {
            User u = opt.get();
            u.setNome(nome);
            u.setPerfil(perfil);
            return usuarioRepository.save(u);
        } else {
            User novo = new User();
            novo.setEmail(email);
            novo.setNome(nome);
            novo.setPerfil(perfil);
            return usuarioRepository.save(novo);
        }
    }
    
}