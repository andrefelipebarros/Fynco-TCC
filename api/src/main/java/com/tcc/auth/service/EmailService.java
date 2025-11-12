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

    private String dashboardUrl = "https://fynco.netlify.app/dashboard";

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
            Email from = new Email(mailFrom, "Equipe Fynco");
            Email to = new Email(toEmail);
            String subject = "Seu perfil de investidor foi definido!";
            
            String htmlContent = buildHtmlEmail(name, profile.toString(), dashboardUrl); 
            Content content = new Content("text/html", htmlContent);

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
                logger.error("Falha ao enviar e-mail via SendGrid. Status={} - Body: {}", statusCode, response.getBody());
            }

        } catch (Exception e) {
            logger.error("Erro ao enviar e-mail via SendGrid: ", e);
        }
    }

    private String buildHtmlEmail(String name, String profile, String ctaUrl) {
        String logoUrl = "https://fynco.netlify.app/logo-transparente.png";
        String escapedName = escapeHtml(name);
        String escapedProfile = escapeHtml(profile);

        String bodyStyle = "style='margin:0;padding:0;font-family:Arial,sans-serif;background-color:#F3F4F6;'";
        String mainTableStyle = "style='width:100%;max-width:600px;margin:0 auto;background-color:#ffffff;border-radius:8px;overflow:hidden;'";
        String headerCellStyle = "style='background-color:#1E2A5E;padding:20px 24px;'";
        String contentCellStyle = "style='padding:32px 24px;color:#1F2937;line-height:1.6;'";
        String footerCellStyle = "style='background-color:#1E2A5E;color:#A9B2D3;padding:24px;text-align:center;font-size:13px;line-height:1.5;'";

        String h1Style = "style='margin:0 0 16px 0;font-size:22px;color:#1E2A5E;font-weight:bold;'";
        String pStyle = "style='margin:0 0 16px 0;color:#4B5563;font-size:16px;'";
        String profileBoxStyle = "style='background-color:#F0FDF4;border:1px solid #38B000;padding:12px 16px;border-radius:6px;text-align:center;margin:24px 0;'";
        String profileTextStyle = "style='color:#0f7a2e;font-size:18px;font-weight:bold;margin:0;'";
        
        String buttonStyle = "style='display:inline-block;background-color:#3B82F6;color:#ffffff;text-decoration:none;padding:12px 20px;border-radius:6px;font-size:16px;font-weight:bold;margin:16px 0 8px 0;'";
        String sloganStyle = "style='color:#4B5563;font-style:italic;margin:24px 0 0 0;font-size:15px;'";
        
        String footerLogoStyle = "style='height:24px;display:block;margin:0 auto 12px auto;opacity:0.9;border:0;'";

        return "<!DOCTYPE html>"
            + "<html lang='pt-BR'>"
            + "<head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'></head>"
            + "<body " + bodyStyle + ">"
            + "<table role='presentation' width='100%' border='0' cellpadding='0' cellspacing='0' style='padding:20px 0;'>"
            + "  <tr>"
            + "    <td>"
            + "      <table role='presentation' align='center' border='0' cellpadding='0' cellspacing='0' " + mainTableStyle + ">"
            + "        <tr>"
            + "          <td " + headerCellStyle + ">"
            + "            <table role='presentation' width='100%' border='0' cellpadding='0' cellspacing='0'>"
            + "              <tr>"
            + "                <td style='width:50%;text-align:left;'>"
            + "                  <img src='" + logoUrl + "' alt='Fynco' style='height:36px;display:block;border:0;'>"
            + "                </td>"
            + "                <td style='width:50%;text-align:right;color:#A9B2D3;font-size:14px;'>"
            + "                  contatofynco@gmail.com"
            + "                </td>"
            + "              </tr>"
            + "            </table>"
            + "          </td>"
            + "        </tr>"
            + "        <tr>"
            + "          <td " + contentCellStyle + ">"
            + "            <h1 " + h1Style + ">Olá, " + escapedName + "!</h1>"
            + "            <p " + pStyle + ">Obrigado por completar nosso questionário. Seu perfil de investidor foi definido como:</p>"
            + "            <div " + profileBoxStyle + ">"
            + "              <p " + profileTextStyle + ">" + escapedProfile + "</p>"
            + "            </div>"
            + "            <p " + pStyle + ">Agora você já pode acessar seu dashboard e começar a explorar.</p>"
            + "            <a href='" + ctaUrl + "' target='_blank' " + buttonStyle + ">Acessar meu Dashboard</a>"
            + "            <p " + sloganStyle + ">&quot;Capacitando decisões inteligentes de investimento.&quot;</p>"
            + "          </td>"
            + "        </tr>"
            + "        <tr>"
            + "          <td " + footerCellStyle + ">"
            + "            <img src='" + logoUrl + "' alt='Icon' " + footerLogoStyle + ">"
            + "            <div>© 2025 Fynco. Todos os direitos reservados.</div>"
            + "          </td>"
            + "        </tr>"
            + "      </table>"
            + "    </td>"
            + "  </tr>"
            + "</table>"
            + "</body>"
            + "</html>";
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
