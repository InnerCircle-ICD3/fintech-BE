package com.fastcampus.backofficemanage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String accessSchemeName = "bearerAuth";
        final String refreshSchemeName = "refreshAuth";

        return new OpenAPI()
                .info(new Info().title("Merchant API")
                        .description("가맹점 인증 및 관리 API 문서")
                        .version("v1.0"))
                .addSecurityItem(new SecurityRequirement().addList(accessSchemeName))
                .components(new Components()
                        .addSecuritySchemes(accessSchemeName,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                        .addSecuritySchemes(refreshSchemeName,
                                new SecurityScheme()
                                        .name("Refresh-Token")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("리프레시 토큰 헤더")));
    }
}
