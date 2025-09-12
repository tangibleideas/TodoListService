package com.tangibleideas.todolistservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record AddTodoItemRequestDTO(String description, @JsonProperty("due_datetime") Instant dueAtInstant) {
}
