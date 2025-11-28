package com.tcc.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tcc.auth.model.user.User;
import com.tcc.auth.model.user.InvestorProfile;
import com.tcc.auth.model.user.EmailAuthorization;
import com.tcc.auth.model.user.dto.UserStatusResponse;

import com.tcc.auth.repository.UserRepository;
import com.tcc.auth.repository.EmailAuthorizationRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final EmailAuthorizationRepository emailAuthRepository;

    public UserService(UserRepository userRepository, EmailService emailService, EmailAuthorizationRepository emailAuthRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.emailAuthRepository = emailAuthRepository;
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
            
            EmailAuthorization auth = ensureEmailAuthorizationExists(user);

            if (auth.isCanSendEmail()) {
                emailService.sendProfileConfirmationEmail(email, name, profile);
            } else {
                System.out.println("Envio de e-mail bloqueado para o usuário: " + email);
            }
        }
    }

    @Transactional
    public void updateEmailPreference(String email, boolean canSendEmail) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        EmailAuthorization authorization = ensureEmailAuthorizationExists(user);

        authorization.setCanSendEmail(canSendEmail);
        emailAuthRepository.save(authorization);
    }

    public boolean getEmailPreference(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return emailAuthRepository.findByUser(user)
                .map(EmailAuthorization::isCanSendEmail)
                .orElse(true); 
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

     private EmailAuthorization ensureEmailAuthorizationExists(User user) {
        return emailAuthRepository.findByUser(user)
                .orElseGet(() -> {
                    EmailAuthorization newAuth = new EmailAuthorization();
                    newAuth.setUser(user);
                    newAuth.setCanSendEmail(true);
                    return emailAuthRepository.save(newAuth);
                });
    }
}
