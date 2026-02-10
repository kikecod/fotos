package com.campamento.fotos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Campamento San Pablo - API de Fotos")
                        .description("""
                                API REST para el campamento juvenil "San Pablo".

                                ## Funcionalidades
                                - **Autenticación**: Registro y login con JWT
                                - **Retos (Challenges)**: Crear y gestionar retos fotográficos diarios
                                - **Submissions**: Subir fotos como respuesta a los retos

                                ## Roles
                                - `ADMIN`: Crea retos y administra el sistema
                                - `USER`: Sube fotos a los retos activos

                                ## Autenticación
                                1. Registrate en `/api/auth/register` o inicia sesión en `/api/auth/login`
                                2. Copia el token JWT de la respuesta
                                3. Haz clic en el botón **Authorize** 🔒 y pega: `Bearer <tu-token>`
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Campamento San Pablo")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Desarrollo local")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingresa tu token JWT. Ejemplo: eyJhbGciOiJIUzI1NiJ9...")));
    }
}
