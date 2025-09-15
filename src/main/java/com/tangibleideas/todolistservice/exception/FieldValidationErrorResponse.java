package com.tangibleideas.todolistservice.exception;

import java.time.Instant;
import java.util.List;

public record FieldValidationErrorResponse(
    int status, String message, Instant timestamp, List<FieldValidationError> errors) {}
