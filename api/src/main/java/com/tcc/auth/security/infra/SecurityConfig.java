package com.tcc.auth.security.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import com.tcc.auth.security.infra.OAuth2SuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegRep) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/api/**").authenticated() 
            .anyRequest().authenticated()
        )
            .oauth2Login(oauth -> {
                oauth.successHandler(oAuth2SuccessHandler);
                Customizer.withDefaults().customize(oauth);
            })
            .logout(logout -> logout
                .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegRep))
            );

            // H2 CONFIG //
            http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
            http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
            //////////////
        return http.build();

    }
    //
    private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegRep) {
        OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegRep);
        // Define para qual URL o usuário será redirecionado APÓS o logout no provedor
        successHandler.setPostLogoutRedirectUri("http://localhost:8080/");
        return successHandler;
    }
}
