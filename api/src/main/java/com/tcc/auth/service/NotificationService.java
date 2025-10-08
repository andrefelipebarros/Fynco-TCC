package com.tcc.auth.service;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationService {

    @Value("${vapid.public.key}")
    private String publicKey;
    @Value("${vapid.private.key}")
    private String privateKey;

    private PushService pushService;

    @PostConstruct
    private void init() throws GeneralSecurityException {
        this.pushService = new PushService(publicKey, privateKey);
    }

    public void sendNotification(Subscription subscription, String payload) {
        try {
            // Aqui você pode customizar o payload (JSON com título, corpo, ícone, etc.)
            pushService.send(new Notification(subscription, payload));
        } catch (GeneralSecurityException | IOException | JoseException | ExecutionException | InterruptedException e) {
            // Logar o erro ou tratar falhas de envio
            e.printStackTrace();
        }
    }
}
