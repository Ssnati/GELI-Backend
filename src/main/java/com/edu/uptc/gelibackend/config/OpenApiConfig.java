package com.edu.uptc.gelibackend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "GeliBackend API",
                version = "1.0",
                contact = @Contact(
                        name = "Santiago Andrés Orjuela López",
                        url = "https://www.linkedin.com/in/santiago-orjuela-lopez/",
                        email = "santiago.orjuela@uptc.edu.co"
                ),
                extensions = {
                        @Extension(
                                name = "Other Contact",
                                properties = {
                                        @ExtensionProperty(name = "name", value = "Edwin Steven Niño Torres"),
                                        @ExtensionProperty(name = "email", value = "edwin.nino04@uptc.edu.co"),
                                        @ExtensionProperty(name = "url", value = "https://www.linkedin.com/in/edwin-steven-niño-torres-292b01282")
                                }
                        )
                }
        ),
        servers = {
                @Server(
                        description = "Local Server",
                        url = "http://localhost:8080"
                )
        },
        security = @SecurityRequirement(
                name = "bearer-jwt"
        )
)
@SecurityScheme(
        name = "bearer-jwt",
        description = """
                For using this API, you need to provide a JWT token in the Authorization header.
                The token is obtained by logging in with the credentials of a user with the role of ADMIN or USER.
                You must authenticate in the Keycloak realm with the corresponding permissions.
                """,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}