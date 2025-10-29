package com.tcc.auth.security.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        // Aqui você pode gerar um token se quiser
        // String token = jwtService.generateToken(authentication);

        String htmlResponse = """
            <!DOCTYPE html>
            <html>
              <body>
                <script>
                  window.opener.postMessage({ type: "OAUTH_SUCCESS" }, window.location.origin);
                  window.close();
                </script>
              </body>
            </html>
            """;

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(htmlResponse);
    }
}
