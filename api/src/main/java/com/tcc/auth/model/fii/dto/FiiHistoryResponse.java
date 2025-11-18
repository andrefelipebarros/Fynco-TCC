package com.tcc.auth.model.fii.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiiHistoryResponse {
    private Integer id;
    private LocalDate dataPagamento;
    private Double valor;
}