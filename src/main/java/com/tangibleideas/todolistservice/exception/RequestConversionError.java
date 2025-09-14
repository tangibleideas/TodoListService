package com.tangibleideas.todolistservice.exception;

public record RequestConversionError(Object value, String message) {
}