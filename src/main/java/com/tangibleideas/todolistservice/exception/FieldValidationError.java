package com.tangibleideas.todolistservice.exception;

public record FieldValidationError(Object value, String message) {}
