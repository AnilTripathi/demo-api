package com.myhealth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Value("${springdoc.info.title}")
    private String title;
    
    @Value("${springdoc.info.description}")
    private String description;
    
    @Value("${springdoc.info.version}")
    private String version;
    
    @Value("${springdoc.info.contact.name}")
    private String contactName;
    
    @Value("${springdoc.info.contact.email}")
    private String contactEmail;
    
    @Value("${springdoc.info.license.name}")
    private String licenseName;
    
    @Value("${springdoc.info.license.url}")
    private String licenseUrl;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version)
                        .contact(new Contact()
                                .name(contactName)
                                .email(contactEmail))
                        .license(new License()
                                .name(licenseName)
                                .url(licenseUrl)))
                .servers(List.of(
                        new Server().url("http://localhost:8089").description("Development server"),
                        new Server().url("https://staging.myhealth.com").description("Staging server"),
                        new Server().url("https://api.myhealth.com").description("Production server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token for API authentication")));
    }
}