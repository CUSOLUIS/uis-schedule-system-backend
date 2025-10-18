package com.uis.schedule.backend.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
  info = @Info(
    title = "UIS Schedule API",
    version = "v1",
    description = "API del sistema de agendamiento"
  )
)
public class OpenApiConfig {}

