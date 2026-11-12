package com.commune.app.socle.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey cle;
    private final long dureeSecondes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-seconds:3600}") long dureeSecondes) {
        if (secret.length() < 32) {
            throw new IllegalArgumentException(
                    "La clé JWT doit contenir au moins 32 caractères.");
        }
        this.cle = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.dureeSecondes = dureeSecondes;
    }

    // Métier : crée un token signé avec l'identifiant de l'utilisateur.
    public String generer(String idUtilisateur) {
        Instant maintenant = Instant.now();
        return Jwts.builder()
                .subject(idUtilisateur)
                .issuedAt(Date.from(maintenant))
                .expiration(Date.from(maintenant.plusSeconds(dureeSecondes)))
                .signWith(cle)
                .compact();
    }

    public long getDureeSecondes() {
        return dureeSecondes;
    }

    public String extraireUtilisateur(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(cle)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException exception) {
            return null;
        }
    }

    public Instant dateExpiration(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(cle)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .toInstant();
        } catch (JwtException | IllegalArgumentException exception) {
            return null;
        }
    }
}
