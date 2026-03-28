package com.uis.schedule.backend.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic API response wrapper for all endpoints.
 * Provides a consistent structure with data, message, and error details.
 *
 * @param <T> The type of the data being returned.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    @JsonProperty("Data")
    private T data;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Errors")
    private List<String> errors;

    /**
     * Helper to create a successful response with data and a message.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .data(data)
                .message(message)
                .errors(java.util.Collections.emptyList())
                .build();
    }

    /**
     * Helper to create an error response with a message and list of errors.
     */
    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return ApiResponse.<T>builder()
                .data(null)
                .message(message)
                .errors(errors)
                .build();
    }
}
