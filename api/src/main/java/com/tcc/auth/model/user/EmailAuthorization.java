package com.tcc.auth.model.user;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "email_authorizations")
@NoArgsConstructor
@AllArgsConstructor
public class EmailAuthorization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
  
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean canSendEmail = true;
}
