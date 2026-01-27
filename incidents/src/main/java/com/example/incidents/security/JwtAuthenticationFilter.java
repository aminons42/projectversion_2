package com.example.incidents.security; // Ton package actuel

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList; // On utilise une liste vide d'autorités pour l'instant

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // On a SUPPRIMÉ UserDetailsService. On n'en a pas besoin ici.
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtUtil.getUsernameFromToken(jwt);

        // On vérifie juste le token, on ne va pas en base de données
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Ici, on valide le token avec la signature
            // Note: J'ai changé la méthode validateToken pour qu'elle prenne juste le username, pas l'objet UserDetails
            if (jwtUtil.validateToken(jwt, username)) {

                // On crée l'objet d'authentification "manuellement"
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, // On met le username comme "principal"
                        null,
                        new ArrayList<>() // Pas de rôles pour l'instant
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}