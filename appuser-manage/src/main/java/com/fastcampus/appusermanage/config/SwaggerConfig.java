package com.fastcampus.appusermanage.config;

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
                .info(new Info()
                        .title("App-User API")
                        .description("유저 인증 및 관리 API 문서")
                        .version("v1.0"))
                // 모든 엔드포인트에 bearerAuth를 전역으로 적용
                .addSecurityItem(new SecurityRequirement().addList(accessSchemeName))
                .components(new Components()
                        .addSecuritySchemes(accessSchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Access Token (Bearer JWT)"))
                        .addSecuritySchemes(refreshSchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Refresh-Token")
                                        .description("Refresh Token 헤더")));
    }
}
