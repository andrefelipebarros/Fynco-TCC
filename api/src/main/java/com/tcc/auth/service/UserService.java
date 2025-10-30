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
            User user = opt.get();
            user.setNome(nome != null ? nome : user.getNome());
            // atualiza perfil somente se vier não-nulo (se quiser forçar atualização, ajuste aqui)
            if (perfil != null) {
                user.setPerfil(perfil);
            }
            return usuarioRepository.save(user);
        } else {
            // Se for novo usuário, perfil é obrigatório (você quer só criar após questionário)
            if (perfil == null) {
                throw new IllegalArgumentException("Perfil é obrigatório para criação de novos usuários.");
            }
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setNome(nome);
            newUser.setPerfil(perfil);
            return usuarioRepository.save(newUser);
        }
    }
    
}