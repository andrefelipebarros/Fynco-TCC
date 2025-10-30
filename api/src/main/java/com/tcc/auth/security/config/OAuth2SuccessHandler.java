package com.tcc.auth.security.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.tcc.auth.security.jwt.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    // injetar JwtUtil
    private final JwtUtil jwtUtil;
    private final String frontendOrigin = "http://localhost:3000";


    public OAuth2SuccessHandler(JwtUtil jwtUtil) {
      this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
      
    OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
    String email = (String) oauthUser.getAttribute("email");
    // claims que você achar necessário
    Map<String, Object> claims = Map.of("email", (Object) email);
    String token = jwtUtil.generateToken(email, claims);
    // HTML que faz postMessage com token
    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out = response.getWriter();
    out.write("""
        <!doctype html>
        <html>
          <body>
            <script>
              try {
                window.opener.postMessage({ type: 'OAUTH2_SUCCESS', token: '%s' },'%s');
              } catch(e) { 
                console.error(e); 
              }
              finally { window.close(); }
            </script>
          </body>
        </html>
      """.formatted(token, frontendOrigin));
    out.flush();
  }
}
