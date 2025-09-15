package com.tangibleideas.todolistservice.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<FieldValidationErrorResponse> handleRequestValidationExceptions(
      MethodArgumentNotValidException ex) {
    log.error(ex.getMessage(), ex);
    List<FieldValidationError> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(this::mapToFieldValidationError)
            .collect(Collectors.toList());
    FieldValidationErrorResponse errorResponse =
        new FieldValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            Instant.now(),
            errors);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageConversionException.class)
  public ResponseEntity<ClientErrorResponse> handleRequestDeserializationExceptions(
      HttpMessageConversionException ex) {
    log.error(ex.getMessage(), ex);
    ClientErrorResponse errorResponse =
        new ClientErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            Instant.now(),
            new ArrayList<>(
                Collections.singleton(
                    new ClientError(
                        "request (uri, headers or payload)",
                        "Malformed or not according to API " + "specifications"))));
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  private FieldValidationError mapToFieldValidationError(FieldError fieldError) {
    return new FieldValidationError(fieldError.getRejectedValue(), fieldError.getDefaultMessage());
  }

  @ExceptionHandler(StatusUpdateNotAllowedException.class)
  public ResponseEntity<ClientErrorResponse> handleStatusUpdateNotAllowedException(
      StatusUpdateNotAllowedException ex) {
    log.error(ex.getMessage(), ex);
    ClientErrorResponse errorResponse =
        new ClientErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            Instant.now(),
            new ArrayList<>(Collections.singleton(new ClientError("status", ex.getMessage()))));
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
}
