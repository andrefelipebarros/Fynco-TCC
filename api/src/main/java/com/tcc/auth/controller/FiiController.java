package com.tcc.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tcc.auth.model.fii.dto.FiiResponse;
import com.tcc.auth.service.FiiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fiis")
@RequiredArgsConstructor
public class FiiController {

    private final FiiService fiiService;

    @GetMapping({"/", ""})
    public ResponseEntity<List<FiiResponse>> listarTodos() {
        List<FiiResponse> fiis = fiiService.listarTodos();
        return ResponseEntity.ok(fiis);
    }
}
