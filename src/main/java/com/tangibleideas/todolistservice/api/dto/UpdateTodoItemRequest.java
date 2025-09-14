package com.tangibleideas.todolistservice.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tangibleideas.todolistservice.domain.TodoItemStatus;
import jakarta.validation.constraints.Size;

import static com.tangibleideas.todolistservice.util.Constants.MAX_DESCRIPTION_LENGTH;

public record UpdateTodoItemRequest(
        @Size(min = 1,
              max = MAX_DESCRIPTION_LENGTH,
              message = "Description must be between 1 and" +
                        MAX_DESCRIPTION_LENGTH + " characters")
        String description,

        @JsonDeserialize(using = TodoItemStatusDeserializer.class)
        TodoItemStatus status
) {
}