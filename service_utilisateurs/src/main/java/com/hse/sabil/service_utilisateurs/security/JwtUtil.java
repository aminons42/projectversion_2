package com.hse.sabil.service_utilisateurs.security;


import com.hse.sabil.service_utilisateurs.model.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component // On dit à Spring de gérer cette classe
public class JwtUtil {

    // 1. On lit la clé secrète depuis application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    // 2. On lit le temps d'expiration depuis application.properties
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Si tu veux ajouter des infos en plus dans le token (ex: nom, rôles)
        // C'est ici que tu le ferais.
        if (userDetails instanceof Utilisateur) {
            Utilisateur user = (Utilisateur) userDetails;
            claims.put("nom", user.getNom());
            claims.put("prenom", user.getPrenom());
        }

        return createToken(claims, userDetails.getUsername());
    }
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims) // Les infos en plus
                .subject(subject) // Le "sujet" (le username)
                .issuedAt(new Date(System.currentTimeMillis())) // Date de création
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Date d'expiration
                .signWith(getSigningKey()) // Signature avec notre clé secrète (algorithme auto-détecté)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject); // Le "Subject" est notre username
    }

    // --- Fonctions "utilitaires" pour lire le token ---

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date()); // Vérifie si la date d'exp. est avant aujourd'hui
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Le "cœur" de la lecture : il utilise la clé secrète pour ouvrir le token
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith( getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Utilitaire pour transformer notre clé "String" en objet "Key"
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
