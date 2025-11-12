package com.tcc.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.tcc.auth.model.user.InvestorProfile;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final SendGrid sendGrid;

    @Value("${spring.mail.username:}")
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
        if (!StringUtils.hasText(mailFrom)) {
            logger.error("Remetente (spring.mail.username) não configurado. Abortando envio.");
            return;
        }

        try {
            Email from = new Email(mailFrom);
            Email to = new Email(toEmail);
            String subject = "Seu perfil de investidor foi definido!";
            String htmlContent = buildHtmlEmail(name, profile.toString());
            Content content = new Content("text/html", htmlContent);

            // Montagem robusta do Mail usando Personalization
            Mail mail = new Mail();
            mail.setFrom(from);
            mail.setSubject(subject);
            mail.addContent(content);

            Personalization personalization = new Personalization();
            personalization.addTo(to);
            mail.addPersonalization(personalization);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            int statusCode = response.getStatusCode();

            if (statusCode >= 200 && statusCode < 300) {
                logger.info("E-mail enviado com sucesso para {} (status={})", toEmail, statusCode);
            } else {
                logger.error("Falha ao enviar e-mail via SendGrid. Status={}", statusCode);
            }

        } catch (Exception e) {
            logger.error("Erro ao enviar e-mail via SendGrid: ", e);
        }
    }

    private String buildHtmlEmail(String name, String profile) {
        // Utilizei Base64 para a imagem.
        String logoUrl = "https://fynco.netlify.app/logo-transparente.png";

        String html = ""
            + "<html lang='pt-BR'>"
            + "<body style='background-color:#F3F4F6;font-family:Arial,sans-serif;margin:0;padding:0;'>"
            + "  <div style='max-width:600px;margin:20px auto;background:#fff;border-radius:8px;overflow:hidden;'>"
            + "    <div style='background:#1E2A5E;padding:20px;display:flex;align-items:center;justify-content:space-between;'>"
            + "      <img src='" + logoUrl + "' alt='Fynco' style='height:40px;display:block;border:0;'>"
            + "      <div style='color:#fff;font-size:14px;'>contatofynco@gmail.com</div>"
            + "    </div>"
            + "    <div style='padding:24px;color:#1F2937;'>"
            + "      <h1 style='margin:0 0 12px 0;font-size:20px;color:#1E2A5E;'>Olá, " + escapeHtml(name) + "!</h1>"
            + "      <p style='margin:0 0 12px 0;color:#4B5563;'>Obrigado por completar nosso questionário. Seu perfil de investidor foi definido como:</p>"
            + "      <div style='background:#F0FDF4;border:1px solid #38B000;padding:12px;border-radius:6px;text-align:center;margin-bottom:16px;'>"
            + "        <strong style='color:#0f7a2e;font-size:18px;'>" + escapeHtml(profile) + "</strong>"
            + "      </div>"
            + "      <p style='color:#4B5563;margin:0 0 8px 0'>Agora você já pode acessar seu dashboard e começar a explorar.</p>"
            + "      <p style='color:#4B5563;font-style:italic;margin:0'>&quot;Capacitando decisões inteligentes de investimento.&quot;</p>"
            + "    </div>"
            + "    <div style='background:#1E2A5E;color:#fff;padding:16px;text-align:center;font-size:13px;'>"
            + "      <img src='" + logoUrl + "' alt='Icon' style='height:24px;display:block;margin:0 auto 8px auto;opacity:0.9;border:0;'>"
            + "      <div>© 2025 Fynco. Todos os direitos reservados.</div>"
            + "    </div>"
            + "  </div>"
            + "</body>"
            + "</html>";
        return html;
    }

    // Pequena função para escapar caracteres HTML básicos
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
