package com.campamento.fotos.config;

import com.campamento.fotos.entity.Role;
import com.campamento.fotos.entity.User;
import com.campamento.fotos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createAdminUserIfNotExists();
    }

    private void createAdminUserIfNotExists() {
        String adminUsername = "admin";
        
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User admin = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode("Hi-alsoWm24"))
                    .role(Role.ADMIN)
                    .build();
            
            userRepository.save(admin);
            log.info("Usuario admin creado exitosamente");
        } else {
            log.info("Usuario admin ya existe");
        }
    }
}
