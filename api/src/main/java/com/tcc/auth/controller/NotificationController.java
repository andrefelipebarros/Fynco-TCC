package com.tcc.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.auth.service.NotificationService;

import nl.martijndwars.webpush.Subscription;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class NotificationController {

    private final NotificationService notificationService;
    // ATENÇÃO: Em produção, use um Banco de Dados para persistir as inscrições!
    private final ConcurrentHashMap<String, Subscription> subscriptions = new ConcurrentHashMap<>();

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody Subscription subscription) {
        System.out.println("Subscribed: " + subscription.endpoint);
        // O ideal é associar a inscrição a um ID de usuário
        this.subscriptions.put(subscription.endpoint, subscription);
    }

    @PostMapping("/send-notification")
    public void sendNotificationToAll() {
        String payload = "{\"title\":\"Olá do Spring Boot!\",\"body\":\"Esta é uma notificação de teste.\"}";

        subscriptions.values().forEach(subscription -> {
            notificationService.sendNotification(subscription, payload);
        });
    }
}