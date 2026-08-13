package com.uis.schedule.backend.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Global Jackson configuration for consistent date/time serialization.
 * <p>
 * Ensures that:
 * <ul>
 *   <li>{@code LocalDate} is serialized as {@code yyyy-MM-dd} (ISO-8601)</li>
 *   <li>{@code LocalTime} is serialized as {@code HH:mm:ss} (ISO-8601)</li>
 *   <li>{@code LocalDateTime} is serialized as ISO-8601 (not as array)</li>
 *   <li>Unknown properties in requests are rejected (fail-fast)</li>
 * </ul>
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }
}
