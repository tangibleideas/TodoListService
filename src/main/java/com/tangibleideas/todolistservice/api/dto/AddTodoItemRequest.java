package com.tangibleideas.todolistservice.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

import static com.tangibleideas.todolistservice.util.Constants.MAX_DESCRIPTION_LENGTH;

public record AddTodoItemRequest(

        @NotBlank(message = "Description is required and can not be blank")
        @Size(min = 1,
              max = MAX_DESCRIPTION_LENGTH,
              message = "Description must be between 1 and" +
                        MAX_DESCRIPTION_LENGTH + " characters")
        String description,

        @JsonProperty("due_datetime")
        @Future(message = "due_datetime must be in future")
        Instant dueAt) {
}
