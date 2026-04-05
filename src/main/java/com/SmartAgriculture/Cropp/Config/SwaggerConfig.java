package com.SmartAgriculture.Cropp.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI(){
        SecurityScheme bearerScheme=new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Bearer Token");

            SecurityRequirement bearerRequirement = new SecurityRequirement()
                                            .addList("Bearer Authentication");
            
            return new OpenAPI()
                       .info(new Info()
                          .title("Smart Agriculture System")
                          .version("1.0")
                          .description("This is Spring Boot Project For Agriculture ")
                          .license(new License().name("Apache 2.0").url("https://www.linkedin.com/in/siddharth-java-dev/"))
                          .contact(new Contact()
                                        .name("Siddharth")
                                        .email("GautamSiddharth@gamil.com")
                                        .url("https://github.com/Siddharth-del")))
                          .externalDocs(new ExternalDocumentation()
                                      .description("Project Documentation")
                                      .url("https://www.linkedin.com/in/siddharth-java-dev/" ))              

                       .components(new Components()
                       .addSecuritySchemes("Bearer Authentication", bearerScheme))
                    .addSecurityItem(bearerRequirement);
    }
}
