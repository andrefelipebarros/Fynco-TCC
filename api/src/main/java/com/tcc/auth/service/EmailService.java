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
        return """
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Confirmação de Inscrição - Fynco</title>
  <style>
    body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
    table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
    img { -ms-interpolation-mode: bicubic; border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; }
    table { border-collapse: collapse !important; }
    body { height: 100% !important; margin: 0 !important; padding: 0 !important; width: 100% !important; }
  </style>
</head>
<body style="background-color: #F3F4F6; font-family: sans-serif; margin: 0 !important; padding: 0 !important;">
  
  <div style="max-width: 32rem; margin-left: auto; margin-right: auto; background-color: #FFFFFF; box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05); border-radius: 0.5rem; overflow: hidden; margin-top: 2.5rem;">
    
    <div style="background-color: #1E2A5E; color: #FFFFFF; padding: 1.5rem; display: flex; align-items: center; justify-content: space-between;">
      <img src="https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-5N9kRcdyl478ovIeXeYOGoH7DyEIVu.png" alt="Fynco Logo" style="height: 2.5rem;">
      <div style="text-align: right; font-size: 0.875rem; line-height: 1.25rem;">
        <p style="font-weight: 500; margin: 0;">contatofynco@gmail.com</p>
      </div>
    </div>

    <div style="padding: 2rem; color: #1F2937;">
      <h1 style="font-size: 1.875rem; line-height: 2.25rem; font-weight: 600; color: #1E2A5E; margin-bottom: 0.75rem; margin-top: 0;">
        Olá, %s!
      </h1>
      <p style="color: #4B5563; margin-bottom: 1rem; margin-top: 0;">
        Obrigado por completar nosso questionário. Seu perfil de investidor foi definido como:
      </p>
      <div style="background-color: #F0FDF4; border: 1px solid #38B000; padding: 1rem; border-radius: 0.375rem; text-align: center; margin-bottom: 1.5rem;">
        <h2 style="color: #38B000; margin: 0;">%s</h2>
      </div>
      <p style="color: #4B5563; margin-top: 2rem; text-align: center; margin-bottom: 0;">
        Agora você já pode acessar seu dashboard e começar a explorar.
      </p>
      <p style="color: #4B5563; margin-top: 2rem; text-align: center; font-style: italic; margin-bottom: 0;">
        "Capacitando decisões inteligentes de investimento."
      </p>
    </div>

    <div style="background-color: #1E2A5E; color: #FFFFFF; padding: 1.5rem; text-align: center; font-size: 0.875rem; line-height: 1.25rem;">
      <img src="https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-5N9kRcdyl478ovIeXeYOGoH7DyEIVu.png" alt="Fynco Icon" style="height: 2rem; margin-left: auto; margin-right: auto; margin-bottom: 0.75rem; opacity: 0.9;">
      <p style="margin: 0;">© 2025 Fynco. Todos os direitos reservados.</p>
    </div>

  </div>
</body>
</html>
""".formatted(name, profile);
    }
}
