package com.julir.starwarsbe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        Map<String, String> errors
) {
    public ErrorResponse(int status, String message) {
        this(LocalDateTime.now(), status, message, null);
    }

    public ErrorResponse(int status, Map<String, String> errors) {
        this(LocalDateTime.now(), status, null, errors);
    }
}
