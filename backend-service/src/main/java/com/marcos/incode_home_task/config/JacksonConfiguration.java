package com.marcos.incode_home_task.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration
{
    @Bean
    ObjectMapper objectMapper()
    {
        return new ObjectMapper().findAndRegisterModules();
    }
}
