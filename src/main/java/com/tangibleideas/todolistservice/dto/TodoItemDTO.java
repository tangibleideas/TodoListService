package com.tangibleideas.todolistservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record TodoItemDTO(
        Long id,
        String description,
        String status,
        @JsonProperty("creation_datetime") Instant createdAtInstant,
        @JsonProperty("due_datetime") Instant dueAtInstant,
        @JsonProperty("done_datetime") Instant doneAtInstant,
        @JsonIgnore Instant updatedAtInstant
) {
}
