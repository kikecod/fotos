package com.campamento.fotos.service;

import com.campamento.fotos.security.JwtService;
import com.campamento.fotos.dto.AuthenticationRequest;
import com.campamento.fotos.dto.AuthenticationResponse;
import com.campamento.fotos.dto.RegisterRequest;
import com.campamento.fotos.entity.Role;
import com.campamento.fotos.entity.User;
import com.campamento.fotos.exception.ApiException;
import com.campamento.fotos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

        private final UserRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        // --- REGISTRO DE USUARIO ---
        public AuthenticationResponse register(RegisterRequest request) {
                // Validar que el username no exista
                if (repository.findByUsername(request.getUsername()).isPresent()) {
                        throw ApiException.conflict(
                                        "El nombre de usuario '" + request.getUsername() + "' ya está en uso");
                }

                // Forzar rol USER (prevenir escalación de privilegios)
                var user = User.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.USER)
                                .build();

                // Guardar en BD
                repository.save(user);

                // Generar Token automáticamente para que ya quede logueado
                var jwtToken = jwtService.generateToken(user);

                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .build();
        }

        // --- LOGIN DE USUARIO ---
        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                // El AuthenticationManager se encarga de verificar usuario y contraseña.
                // Si la contraseña es incorrecta, lanza BadCredentialsException.
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getUsername(),
                                                request.getPassword()));

                // Si llegamos aquí, el usuario y contraseña son correctos.
                var user = repository.findByUsername(request.getUsername())
                                .orElseThrow();

                // Generamos el token
                var jwtToken = jwtService.generateToken(user);

                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .build();
        }
}
