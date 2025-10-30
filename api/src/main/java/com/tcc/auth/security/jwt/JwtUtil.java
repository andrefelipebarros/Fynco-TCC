package com.tcc.auth.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    // Troque por propriedade via application.yml/prod secrets
    private final Key key = Keys.hmacShaKeyFor("SUA_CHAVE_SECRETA_MUITO_GRANDE_ALTERE_AQUI".getBytes());
    private final long expirationMs = 1000L * 60 * 60 * 24 * 7; // 7 dias

    public String generateToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public Jws<Claims> validate(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}