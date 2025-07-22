package com.zerobase.spendingalertservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    final String securitySchemeName = "bearer-key";  //

    return new OpenAPI()
        .info(new Info()
            .title("Spending Alert Service API")
            .description("신용카드 소비 분석 및 한도 초과 알림 서비스")
            .version("v1"))
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
        .components(new io.swagger.v3.oas.models.Components()
            .addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
  }
}
