package com.tangibleideas.todolistservice.api.controller;

import com.tangibleideas.todolistservice.api.dto.AddTodoItemRequest;
import com.tangibleideas.todolistservice.api.dto.TodoItemDTO;
import com.tangibleideas.todolistservice.api.dto.UpdateTodoItemRequest;
import com.tangibleideas.todolistservice.service.TodoItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoItemController {

    private final TodoItemService todoItemService;

    @GetMapping("/{id}")
    public ResponseEntity<TodoItemDTO> getTodoItem(
            @PathVariable
            Long id) {
        return todoItemService
                .findItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity
                                .notFound()
                                .build());
    }

    @GetMapping
    public List<TodoItemDTO> getTodoItems(
            @RequestParam(required = false,
                          defaultValue = "false")
            boolean all) {
        if (all) {
            return todoItemService.getAllItems();
        }
        return todoItemService.getNotDoneItems();
    }

    @PostMapping
    public ResponseEntity<TodoItemDTO> addTodoItem(
            @Valid
            @RequestBody
            AddTodoItemRequest todoItem) {
        TodoItemDTO createdTodoItem = todoItemService.addItem(todoItem);
        return ResponseEntity
                .created(ServletUriComponentsBuilder
                                 .fromCurrentRequest()
                                 .path("/{id}")
                                 .buildAndExpand(createdTodoItem.id())
                                 .toUri())
                .body(createdTodoItem);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<TodoItemDTO> updateTodoItem(
            @PathVariable
            Long id,
            @Valid
            @RequestBody
            UpdateTodoItemRequest todoItem) {
        return todoItemService
                .updateItem(id, todoItem)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity
                                .notFound()
                                .build());

    }


}
