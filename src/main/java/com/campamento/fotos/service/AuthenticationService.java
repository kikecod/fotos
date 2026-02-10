package com.campamento.fotos.service;

import com.campamento.fotos.security.JwtService;
import com.campamento.fotos.dto.AuthenticationRequest;
import com.campamento.fotos.dto.AuthenticationResponse;
import com.campamento.fotos.dto.RegisterRequest;
import com.campamento.fotos.entity.Role;
import com.campamento.fotos.entity.User;
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
                // 1. Crear el objeto Usuario
                var user = User.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword())) // ¡Nunca guardar texto plano!
                                .role(request.getRole())
                                .build();

                // 2. Guardar en BD
                repository.save(user);

                // 3. Generar Token automáticamente para que ya quede logueado
                var jwtToken = jwtService.generateToken(user);

                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .build();
        }

        // --- LOGIN DE USUARIO ---
        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                // 1. El AuthenticationManager se encarga de verificar usuario y contraseña.
                // Si la contraseña es incorrecta, lanza una excepción automáticamente.
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getUsername(),
                                                request.getPassword()));

                // 2. Si llegamos aquí, es que el usuario y contraseña son correctos.
                // Buscamos al usuario en la BD para generar el token.
                var user = repository.findByUsername(request.getUsername())
                                .orElseThrow();

                // 3. Generamos el token
                var jwtToken = jwtService.generateToken(user);

                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .build();
        }
}
