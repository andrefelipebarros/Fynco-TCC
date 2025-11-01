package com.tcc.auth.security.Handlers;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final OidcClientInitiatedLogoutSuccessHandler delegate;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public CustomLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository,
                                      OAuth2AuthorizedClientService authorizedClientService,
                                      String postLogoutRedirectUri) {
        this.delegate = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        this.delegate.setPostLogoutRedirectUri(postLogoutRedirectUri);
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) {
        try {
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String registrationId = oauthToken.getAuthorizedClientRegistrationId(); // ex: "google"
                String principalName = oauthToken.getName();

                OAuth2AuthorizedClient client = authorizedClientService
                        .loadAuthorizedClient(registrationId, principalName);

                if (client != null && client.getAccessToken() != null) {
                    String accessToken = client.getAccessToken().getTokenValue();
                    // 1) tenta revogar o token no Google (opcional — não é estritamente necessário)
                    try {
                        HttpClient httpClient = HttpClient.newHttpClient();
                        HttpRequest revokeReq = HttpRequest.newBuilder()
                                .uri(URI.create("https://oauth2.googleapis.com/revoke?token=" + accessToken))
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .POST(HttpRequest.BodyPublishers.noBody())
                                .build();
                        httpClient.send(revokeReq, HttpResponse.BodyHandlers.discarding());
                    } catch (Exception e) {
                        // erro na revogação não bloqueia o logout
                        e.printStackTrace();
                    }

                    // 2) remove do store do servidor
                    authorizedClientService.removeAuthorizedClient(registrationId, principalName);
                }
            }
        } catch (Exception ex) {
            // não quebrar o logout por causa de falha aqui
            ex.printStackTrace();
        }

        // delega para o handler OIDC (faz redirect para postLogoutRedirectUri)
        try {
            delegate.onLogoutSuccess(request, response, authentication);
        } catch (Exception e) {
            // fallback: apenas redireciona para home
            try { response.sendRedirect("/"); } catch (Exception ignore) {}
        }
    }
}