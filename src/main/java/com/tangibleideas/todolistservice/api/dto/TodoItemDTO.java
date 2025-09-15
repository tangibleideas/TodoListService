package com.tangibleideas.todolistservice.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tangibleideas.todolistservice.domain.TodoItemStatus;
import java.time.Instant;

public record TodoItemDTO(
    Long id,
    String description,
    @JsonDeserialize(using = TodoItemStatusDeserializer.class) TodoItemStatus status,
    @JsonProperty("creation_datetime") Instant createdAt,
    @JsonProperty("due_datetime") Instant dueAt,
    @JsonProperty("done_datetime") Instant doneAt,
    @JsonIgnore Instant updatedAt) {}
