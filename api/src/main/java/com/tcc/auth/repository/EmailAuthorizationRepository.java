package com.tcc.auth.repository;

import com.tcc.auth.model.user.EmailAuthorization;
import com.tcc.auth.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailAuthorizationRepository extends JpaRepository<EmailAuthorization, UUID> {
    // Busca a autorização baseada na entidade User
    Optional<EmailAuthorization> findByUser(User user);
}
