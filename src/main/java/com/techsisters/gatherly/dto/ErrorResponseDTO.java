package com.techsisters.gatherly.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * Body returned for every failed request handled by GlobalExceptionHandler.
 * Extends ResponseDTO so the frontend can always read { success, message }.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDTO extends ResponseDTO {

    // Field name -> error message; only present for validation failures.
    private Map<String, String> errors;

    public ErrorResponseDTO(String message) {
        super(false, message);
    }

    public ErrorResponseDTO(String message, Map<String, String> errors) {
        super(false, message);
        this.errors = errors;
    }
}
