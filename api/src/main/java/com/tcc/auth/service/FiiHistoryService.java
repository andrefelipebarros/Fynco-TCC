package com.tcc.auth.service;

import com.tcc.auth.model.fii.dto.FiiHistoryResponse;
import com.tcc.auth.repository.FiiHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FiiHistoryService {

    private final FiiHistoryRepository repository;

    public List<FiiHistoryResponse> listarPorId(Integer id) {
        return repository.findByFii_IdOrder(id)
                .stream()
                .map(h -> new FiiHistoryResponse(h.getId(), h.getDataPagamento(), h.getValor()))
                .collect(Collectors.toList());
    }

    public List<FiiHistoryResponse> listarPorTicker(String ticker) {
        return repository.findByFii_TickerIgnoreCaseOrder(ticker)
                .stream()
                .map(h -> new FiiHistoryResponse(h.getId(), h.getDataPagamento(), h.getValor()))
                .collect(Collectors.toList());
    }
}
