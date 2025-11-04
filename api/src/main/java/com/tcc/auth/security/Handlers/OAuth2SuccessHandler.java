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
    private final String frontendOrigin = "https://fynco.netlify.app";

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
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setCompletedQuestionnaire(false);
            userService.save(newUser);
            existing = userService.findByEmail(email);
        }

        boolean completed = existing.map(User::isCompletedQuestionnaire).orElse(false);
        String redirect = completed ? "/dashboard" : "/questionnaire";

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.write("<!doctype html><html><head><meta charset='utf-8'><title>Login success</title></head><body>");
        out.write("<script>");
        out.write("  (function(){");
        out.write("    try {");
        out.write("      var target = '" + frontendOrigin + "';");
        out.write("      if (window.opener) {");
        out.write("        window.opener.postMessage({ type: 'OAUTH_SUCCESS', redirect: '" + redirect + "' }, target);");
        out.write("        window.close();");
        out.write("      } else {");
        out.write("        window.location.href = target + '" + redirect + "';");
        out.write("      }");
        out.write("    } catch (e) {");
        out.write("      console.error(e);");
        out.write("      try { window.location.href = '" + frontendOrigin + redirect + "'; } catch (ex) { /* nada */ }");
        out.write("    }");
        out.write("  })();");
        out.write("</script>");
        out.write("</body></html>");
        out.flush();
    }
}
