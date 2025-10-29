package com.tcc.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class HomeController {

    @GetMapping({"/", ""})
    public ResponseEntity<Void> redirectToFrontend() {
        // Cria o cabeçalho "Location" com a URL de destino
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:3000/questionnaire"));

        // Retorna uma resposta com status 302 (Found) e o cabeçalho
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
