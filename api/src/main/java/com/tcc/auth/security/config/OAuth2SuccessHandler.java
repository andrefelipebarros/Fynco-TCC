package com.tcc.auth.security.config;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.tcc.auth.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    private final String frontendOrigin = "http://localhost:3000";

    public OAuth2SuccessHandler(UserService userService) {
      this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        // Opcional: cria/atualiza um registro mínimo aqui (sem perfil final).
        // Se preferir não criar usuário automaticamente, comente/remova a linha abaixo.
        try {
            userService.saveOrUpdateByEmail(email, name, null);
        } catch (Exception e) {
            // Apenas log para não impedir o fluxo de postMessage
            e.printStackTrace();
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Envia mensagem para a origem exata do frontend (mais seguro que "*")
        out.write("""
            <!DOCTYPE html>
            <html>
              <head>
                <meta charset="utf-8" />
                <title>Logging in...</title>
              </head>
              <body>
                <script>
                  try {
                    if (window.opener) {
                      window.opener.postMessage({ type: "OAUTH_SUCCESS", redirect: "/questionnaire" }, "%s");
                    }
                  } catch(e) {
                    console.error("postMessage error:", e);
                  } finally {
                    window.close();
                  }
                </script>
              </body>
            </html>
            """.formatted(frontendOrigin));

        out.flush();
    }
}
