package com.tangibleideas.todolistservice.exception;

public record ClientError(Object value, String message) {
}