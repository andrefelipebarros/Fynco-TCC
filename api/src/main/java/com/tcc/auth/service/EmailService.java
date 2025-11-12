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
        String logoUrl = "data:image/png;base64,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";

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
