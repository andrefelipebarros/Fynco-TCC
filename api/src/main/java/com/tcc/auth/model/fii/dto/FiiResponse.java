package com.tcc.auth.model.fii.dto;

import com.tcc.auth.model.user.InvestorProfile;

public record FiiResponse(
    Integer id,
    String ticker,
    String nome,
    String setor,
    Double precoAtual,
    Double dy,
    Double pVp,
    InvestorProfile perfil
) {}