package com.tcc.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tcc.auth.model.fii.Fii;
import com.tcc.auth.model.fii.dto.FiiResponse;
import com.tcc.auth.repository.FiiRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FiiService {

    private final FiiRepository fiiRepository;

    public FiiService(FiiRepository fiiRepository) {
        this.fiiRepository = fiiRepository;
    }

    public List<FiiResponse> listarTodos() {
        return fiiRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private FiiResponse toResponse(Fii fii) {
        return new FiiResponse(
            fii.getId(),
            fii.getTicker(),
            fii.getNome(),
            fii.getSetor(),
            fii.getPrecoAtual(),
            fii.getDy(),
            fii.getPVp(),
            fii.getPerfil()
        );
    }
}
