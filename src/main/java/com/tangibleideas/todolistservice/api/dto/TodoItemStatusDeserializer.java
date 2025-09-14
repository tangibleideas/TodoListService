package com.tangibleideas.todolistservice.api.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.tangibleideas.todolistservice.domain.TodoItemStatus;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class TodoItemStatusDeserializer extends JsonDeserializer<TodoItemStatus> {

    private JsonParser p;

    @Override
    public TodoItemStatus deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException {
        Set<String> allowedValues = new HashSet<String>() {{
            add(TodoItemStatus.DONE.getLabel());
            add(TodoItemStatus.NOT_DONE.getLabel());
        }};
        String value = p.getText();
        if (allowedValues.contains(value)) {
            return TodoItemStatus.fromLabel(value);
        }
        String errorMessage =
                "Expected one of " + allowedValues + ", received " + value;
        throw new RuntimeException(errorMessage);
    }
}