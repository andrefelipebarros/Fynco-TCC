package com.tcc.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tcc.auth.model.user.InvestorProfile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendProfileConfirmationEmail(String toEmail, String name, InvestorProfile profile) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Seu perfil de investidor foi definido!");

            // Cria o corpo do e-mail em HTML
            String htmlBody = buildHtmlEmail(name, profile.toString());
            helper.setText(htmlBody, true);

            javaMailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Erro ao enviar e-mail: " + e.getMessage());
        }
    }

    private String buildHtmlEmail(String name, String profile) {
        return "<html lang='pt-br'>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>" +
                "<div style='max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;'>" +
                "<h2 style='color: #333;'>Olá, " + name + "!</h2>" +
                "<p>Obrigado por completar nosso questionário.</p>" +
                "<p>Seu perfil de investidor foi definido como:</p>" +
                "<div style='background-color: #f4f4f4; padding: 10px 20px; border-radius: 5px; text-align: center;'>" +
                "<h3 style='color: #0056b3; margin: 0;'>" + profile + "</h3>" +
                "</div>" +
                "<p style='margin-top: 20px;'>Agora você já pode acessar seu dashboard e começar a explorar.</p>" +
                "<p>Atenciosamente,<br>Equipe Fynco</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
