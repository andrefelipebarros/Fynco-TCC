package com.tcc.auth.model.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProfileRequest {
    @NotBlank
    private String nome;

    // aceita CONSERVATOR, MODERATE, AGRESSIVE (case-insensitive)
    @NotBlank
    @Pattern(regexp = "^(?i)(CONSERVATOR|MODERATE|AGRESSIVE)$", message = "Perfil inválido")
    private String perfil;
}