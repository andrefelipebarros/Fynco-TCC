package com.tcc.auth.model.fii;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "fiis_history")
@NoArgsConstructor
@AllArgsConstructor
public class FiiHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fiis_id", nullable = false)
    private Fii fii;

    @Column(name = "data_pagamento", nullable = false)
    private LocalDate dataPagamento;

    @Column(nullable = false)
    private Double valor;
}