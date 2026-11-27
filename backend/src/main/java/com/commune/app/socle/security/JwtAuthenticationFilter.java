package com.commune.app.socle.security;

import com.commune.app.socle.service.SessionAuthentificationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SessionAuthentificationService sessionService;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            SessionAuthentificationService sessionService,
         CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.sessionService = sessionService;
        this.customUserDetailsService = customUserDetailsService;
    }

    // Métier : place uniquement un utilisateur avec JWT et session valides dans le
    // contexte.
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = jwtService.extraireToken(request).orElse(null);
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

        UserDetails userDetails;
        try {
            userDetails = customUserDetailsService.loadUserById(idUtilisateur);
        } catch (IllegalStateException exception) {
            return;
        }

        if (!userDetails.isEnabled()) {
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
