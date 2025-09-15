package com.tangibleideas.todolistservice.exception;

public class StatusUpdateNotAllowedException extends RuntimeException {
  public StatusUpdateNotAllowedException(String message) {
    super(message);
  }
}
