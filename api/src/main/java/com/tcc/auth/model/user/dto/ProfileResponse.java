package com.tcc.auth.model.user.dto;

import com.tcc.auth.model.user.InvestorProfile;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileResponse {
    private Long id;
    private String email;
    private String nome;
    private InvestorProfile perfil;
}