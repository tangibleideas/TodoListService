package com.tangibleideas.todolistservice.exception;

import java.time.Instant;
import java.util.List;

public record RequestConversionErrorResponse(int status, String message,
                                             Instant timestamp,
                                             List<RequestConversionError> errors) {
}
