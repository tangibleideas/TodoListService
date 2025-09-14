package com.tangibleideas.todolistservice.exception;

import java.time.Instant;
import java.util.List;

public record ClientErrorResponse(int status, String message,
                                  Instant timestamp,
                                  List<ClientError> errors) {
}
