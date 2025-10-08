package com.tcc.auth.model.user.dto;

import java.util.UUID;

import com.tcc.auth.model.user.InvestorProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private UUID id;
    private String email;
    private String nome;
    private InvestorProfile perfil;
}