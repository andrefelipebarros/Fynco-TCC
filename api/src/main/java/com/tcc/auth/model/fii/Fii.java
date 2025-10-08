package com.tcc.auth.model.fii;

import com.tcc.auth.model.user.InvestorProfile;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "fiis")
@NoArgsConstructor
@AllArgsConstructor
public class Fii {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String ticker;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String setor;

    @Column(name = "preco_atual", nullable = false)
    private Double precoAtual;

    @Column(nullable = false)
    private Double dy; // Dividend Yield

    @Column(name = "p_vp", nullable = false)
    private Double pVp; // Preço/Valor Patrimonial

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestorProfile perfil;
}