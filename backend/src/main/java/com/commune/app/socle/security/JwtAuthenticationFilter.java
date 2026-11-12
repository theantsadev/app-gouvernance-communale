package com.commune.app.socle.security;

import com.commune.app.socle.entity.Utilisateur;
import com.commune.app.socle.repository.UtilisateurRepository;
import com.commune.app.socle.service.SessionAuthentificationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SessionAuthentificationService sessionService;
    private final UtilisateurRepository utilisateurRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            SessionAuthentificationService sessionService,
            UtilisateurRepository utilisateurRepository) {
        this.jwtService = jwtService;
        this.sessionService = sessionService;
        this.utilisateurRepository = utilisateurRepository;
    }

    // Métier : place uniquement un utilisateur avec JWT et session valides dans le contexte.
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = extraireToken(request);
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            authentifier(token, request);
        }
        filterChain.doFilter(request, response);
    }

    private void authentifier(String token, HttpServletRequest request) {
        String idUtilisateur = jwtService.extraireUtilisateur(token);
        if (idUtilisateur == null
                || jwtService.dateExpiration(token) == null
                || !sessionService.estValide(token)) {
            return;
        }

        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur).orElse(null);
        if (utilisateur == null || !utilisateur.isActif()) {
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(idUtilisateur, null, java.util.List.of());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String extraireToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring("Bearer ".length()).trim();
        return token.isEmpty() ? null : token;
    }
}
