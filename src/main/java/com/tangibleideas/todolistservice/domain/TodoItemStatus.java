package com.tangibleideas.todolistservice.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum TodoItemStatus {
    NOT_DONE("not done"),
    DONE("done"),
    PAST_DUE("past due");

    private final String label;

    @JsonValue
    public String getLabel() {
        return label;
    }

    public static TodoItemStatus fromLabel(String label) {
        return Stream
                .of(TodoItemStatus.values())
                .filter(status -> status.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown status label : " + label));
    }

    TodoItemStatus(String label) {
        this.label = label;
    }

}
