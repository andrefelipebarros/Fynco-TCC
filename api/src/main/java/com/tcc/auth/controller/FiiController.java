package com.tcc.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tcc.auth.model.fii.dto.FiiHistoryResponse;
import com.tcc.auth.model.fii.dto.FiiResponse;
import com.tcc.auth.service.FiiHistoryService;
import com.tcc.auth.service.FiiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fiis")
@RequiredArgsConstructor
public class FiiController {

    private final FiiService fiiService;

    private final FiiHistoryService fiiHistoryService;

    @GetMapping({"/", ""})
    public ResponseEntity<List<FiiResponse>> listarTodos() {
        List<FiiResponse> fiis = fiiService.listarTodos();
        return ResponseEntity.ok(fiis);
    }

    @GetMapping("/{identifier}/history")
    public ResponseEntity<List<FiiHistoryResponse>> listarHistorico(@PathVariable String identifier) {

        List<FiiHistoryResponse> resultado;

        try {
            // se for número = id
            Integer id = Integer.valueOf(identifier);
            resultado = fiiHistoryService.listarPorId(id);

        } catch (NumberFormatException e) {
            // se for string = ticker
            resultado = fiiHistoryService.listarPorTicker(identifier);
        }

        return ResponseEntity.ok(resultado);
    }
}
