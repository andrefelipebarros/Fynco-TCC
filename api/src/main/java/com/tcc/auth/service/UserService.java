package com.tcc.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tcc.auth.model.user.InvestorProfile;
import com.tcc.auth.model.user.User;
import com.tcc.auth.model.user.dto.UserStatusResponse;
import com.tcc.auth.repository.UserRepository;
    
import jakarta.transaction.Transactional;

@Service
public class UserService {
    
    private final EmailService emailService;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
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
            emailService.sendProfileConfirmationEmail(email, name, profile);
        }
    }

    public UserStatusResponse getUserStatusByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(user -> new UserStatusResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProfile(),
                user.isCompletedQuestionnaire()
            ))
            .orElse(new UserStatusResponse(null, email, null, null, false));
    }
}
