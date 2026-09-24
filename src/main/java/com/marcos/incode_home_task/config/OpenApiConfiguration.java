package com.marcos.incode_home_task.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration
{
    @Bean
    OpenAPI incodeHomeTaskOpenApi()
    {
        return new OpenAPI()
                .info(new Info()
                        .title("Incode Home Task API")
                        .version("v1")
                        .description("Company verification and third-party search API."));
    }
}
