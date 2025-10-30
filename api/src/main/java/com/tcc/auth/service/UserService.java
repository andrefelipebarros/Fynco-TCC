package com.tcc.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tcc.auth.model.user.InvestorProfile;
import com.tcc.auth.model.user.User;
import com.tcc.auth.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void completeQuestionnaire(String email, String name, InvestorProfile profile) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(name);
            user.setProfile(profile);
            user.setCompletedQuestionnaire(true);
            userRepository.save(user);
        }
    }
}
