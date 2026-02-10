package com.campamento.fotos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Validar que el header exista y empiece con "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Extraer el token (quitar "Bearer " del inicio)
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUsername(jwt);

            // 3. Si hay usuario y NO está autenticado todavía en el contexto
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Cargar usuario desde la BD
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 4. Validar token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Crear objeto de autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 5. ESTABLECER LA SEGURIDAD
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token inválido, expirado, malformado, etc.
            // No autenticar al usuario — Spring Security devolverá 401/403 según
            // corresponda
            log.warn("JWT inválido: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
