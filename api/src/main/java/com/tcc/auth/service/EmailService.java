package com.tcc.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.tcc.auth.model.user.InvestorProfile;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final SendGrid sendGrid;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public EmailService(@Value("${SENDGRID_API_KEY}") String sendGridApiKey) {
        this.sendGrid = new SendGrid(sendGridApiKey);
    }

    public void sendProfileConfirmationEmail(String toEmail, String name, InvestorProfile profile) {
        logger.info("Enviando e-mail via SendGrid para {}", toEmail);

        if (toEmail == null || toEmail.isBlank()) {
            logger.warn("E-mail de destino vazio. Abortando envio.");
            return;
        }

        try {
            Email from = new Email(mailFrom);
            Email to = new Email(toEmail);
            String subject = "Seu perfil de investidor foi definido!";
            String htmlContent = buildHtmlEmail(name, profile.toString());
            Content content = new Content("text/html", htmlContent);

            Mail mail = new Mail(from, subject, to, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            int statusCode = response.getStatusCode();

            if (statusCode >= 200 && statusCode < 300) {
                logger.info("E-mail enviado com sucesso para {} (status={})", toEmail, statusCode);
            } else {
                logger.error("Falha ao enviar e-mail via SendGrid. Status={}, Body={}", statusCode, response.getBody());
            }

        } catch (Exception e) {
            logger.error("Erro ao enviar e-mail via SendGrid: ", e);
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
