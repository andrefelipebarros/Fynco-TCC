package com.tcc.auth.security.config;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    // removi o UserService daqui de propósito já que não vamos criar usuário aqui
    private final String frontendOrigin = "http://localhost:3000";

    public OAuth2SuccessHandler() {
      // construtor vazio
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        // Pegamos atributos do OAuth (mas NÃO vamos salvar nada no DB)
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

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
