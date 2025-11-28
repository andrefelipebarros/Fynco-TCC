package com.tcc.auth.controller;

import com.tcc.auth.model.user.dto.EmailPreferenceRequest;
import com.tcc.auth.service.NotificationService;
import com.tcc.auth.service.UserService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    private final ConcurrentHashMap<String, Subscription> pushSubscriptions = new ConcurrentHashMap<>();

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    // --- ÁREA DE WEB PUSH ---

    @PostMapping("/subscribe-push")
    public void subscribeToPush(@RequestBody Subscription subscription) {
        System.out.println("Subscribed to Push: " + subscription.endpoint);
        this.pushSubscriptions.put(subscription.endpoint, subscription);
    }

    @PostMapping("/send-push-test")
    public void sendPushToAll() {
        String payload = "{\"title\":\"Fynco Alert\",\"body\":\"Teste de notificação push!\"}";
        pushSubscriptions.values().forEach(subscription -> {
            try {
                notificationService.sendNotification(subscription, payload);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // --- ÁREA DE PREFERÊNCIAS DE EMAIL ---

    @PutMapping("/email-preference")
    public ResponseEntity<?> updateEmailPreference(
            @AuthenticationPrincipal OAuth2User oauthUser,
            @RequestBody EmailPreferenceRequest request) {

        if (oauthUser == null) {
            return ResponseEntity.status(401).build();
        }

        String email = oauthUser.getAttribute("email");
        
        try {
            userService.updateEmailPreference(email, request.enable()); 
            
            return ResponseEntity.ok(Map.of("message", "Preferência atualizada: " + request.enable()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/email-preference")
    public ResponseEntity<?> getEmailPreference(@AuthenticationPrincipal OAuth2User oauthUser) {
        if (oauthUser == null) {
            return ResponseEntity.status(401).build();
        }

        String email = oauthUser.getAttribute("email");
        
        boolean canSend = userService.getEmailPreference(email);

        return ResponseEntity.ok(Map.of("canSendEmail", canSend));
    }
}
