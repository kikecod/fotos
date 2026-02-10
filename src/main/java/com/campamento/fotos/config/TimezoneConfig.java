package com.campamento.fotos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimezoneConfig {

    /**
     * Configura el Clock de la aplicación con la zona horaria de Bolivia (UTC-4).
     * Esto asegura que todas las operaciones con LocalDateTime.now(clock)
     * usen la hora local de Bolivia para cerrar dinámicas correctamente.
     */
    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("America/La_Paz"));
    }
}
