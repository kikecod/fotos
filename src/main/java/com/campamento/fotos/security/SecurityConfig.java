package com.campamento.fotos.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                // --- OPTIONS para CORS preflight ---
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                // --- Públicas (sin auth) ---
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/api/test/health").permitAll()
                                                .requestMatchers("/h2-console/**").permitAll()
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/v3/api-docs/**",
                                                                "/webjars/**")
                                                .permitAll()
                                                .requestMatchers("/uploads/**").permitAll()
                                                .requestMatchers("/api/submissions/public").permitAll()
                                                .requestMatchers("/api/gallery").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/challenges").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/challenges/days").permitAll()

                                                // --- ADMIN ---
                                                .requestMatchers(HttpMethod.POST, "/api/challenges")
                                                .hasAuthority("ADMIN")
                                                .requestMatchers("/api/submissions/all").hasAuthority("ADMIN")

                                                // --- USER y ADMIN (equipos y admins pueden subir fotos) ---
                                                .requestMatchers(HttpMethod.POST, "/api/challenges/*/submit")
                                                .hasAnyAuthority("USER", "ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/challenges/*/submit")
                                                .hasAnyAuthority("USER", "ADMIN")
                                                .requestMatchers("/api/submissions/my").hasAnyAuthority("USER", "ADMIN")

                                                // --- Todo lo demás requiere auth ---
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOriginPatterns(Arrays.asList(
                                "http://localhost:3000",
                                "http://localhost:5173",
                                "https://campa-frontend.vercel.app",
                                "https://*.vercel.app",
                                "https://*.onrender.com"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                configuration.setAllowedHeaders(
                                Arrays.asList("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
                configuration.setExposedHeaders(Arrays.asList("Authorization"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L); // Cache preflight por 1 hora

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}