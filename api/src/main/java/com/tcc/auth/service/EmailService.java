package com.tcc.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tcc.auth.model.user.InvestorProfile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendProfileConfirmationEmail(String toEmail, String name, InvestorProfile profile) {
        if (toEmail == null || toEmail.isBlank()) {
            logger.warn("E-mail de destino vazio. Abortando envio.");
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setFrom("no-reply@fynco.com");
            helper.setSubject("Seu perfil de investidor foi definido!");
            helper.setText(buildHtmlEmail(name, profile.toString()), true);

            javaMailSender.send(message);
            logger.info("E-mail enviado para {}", toEmail);
        } catch (MessagingException | MailException e) {
            logger.error("Erro ao enviar e-mail para {}: ", toEmail, e);
        } catch (Exception e) {
            logger.error("Erro inesperado ao enviar e-mail: ", e);
        }
    }

    private String buildHtmlEmail(String name, String profile) {
        return "<html lang='pt-br'>"
                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "<div style='max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;'>"
                + "<h2 style='color: #333;'>Olá, " + name + "!</h2>"
                + "<p>Obrigado por completar nosso questionário.</p>"
                + "<p>Seu perfil de investidor foi definido como:</p>"
                + "<div style='background-color: #f4f4f4; padding: 10px 20px; border-radius: 5px; text-align: center;'>"
                + "<h3 style='color: #0056b3; margin: 0;'>" + profile + "</h3>"
                + "</div>"
                + "<p style='margin-top: 20px;'>Agora você já pode acessar seu dashboard e começar a explorar.</p>"
                + "<p>Atenciosamente,<br>Equipe Fynco</p>"
                + "</div>"
                + "</body>"
                + "</html>";
    }
}
