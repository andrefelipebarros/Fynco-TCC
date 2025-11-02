package com.tcc.auth.security.Handlers;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.tcc.auth.model.user.User;
import com.tcc.auth.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final String frontendOrigin = "http://localhost:3000";

    public OAuth2SuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        Optional<User> existing = userService.findByEmail(email);
        if (existing.isEmpty()) {
            // Cria usuário sem nome e perfil inicialmente (usando construtor padrão + setters)
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setCompletedQuestionnaire(false);
            userService.save(newUser);
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String html = "<!doctype html><html><head><meta charset='utf-8'><title>Login success</title></head><body>" +
                "<script>" +
                "  (function(){ " +
                "    try {" +
                "      var frontend = '" + frontendOrigin + "'; " +
                "      if (window.opener) {" +
                "        window.opener.postMessage({ type: 'OAUTH_SUCCESS', redirect: '/questionnaire' }, frontend);" +
                "        window.close();" +
                "      } else {" +
                "        // fallback - se não houver opener, redireciona o próprio popup para o frontend" +
                "        window.location.href = frontend + '/questionnaire';" +
                "      }" +
                "    } catch (e) {" +
                "      console.error(e);" +
                "      window.location.href = '" + frontendOrigin + "/questionnaire';" +
                "    }" +
                "  })();" +
                "</script>" +
                "</body></html>";

        out.write(html);
        out.flush();
    }
}
