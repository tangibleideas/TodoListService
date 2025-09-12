package com.tangibleideas.todolistservice.controller;

import com.tangibleideas.todolistservice.dto.AddTodoItemRequestDTO;
import com.tangibleideas.todolistservice.dto.TodoItemDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1")
public class TodoItemController {

	@GetMapping(value = "/todos/{id}")
	public ResponseEntity<TodoItemDTO> getTodoItem(@PathVariable Long id) {
		return ResponseEntity.ok(new TodoItemDTO(
                id,
                "Description",
                "done",
                Instant.now(),
                Instant.now(),
                null,
                Instant.now()

        ));
	}

	@PostMapping(value = "/todos")
	public ResponseEntity<TodoItemDTO> addTodoItem(@RequestBody AddTodoItemRequestDTO todoItem) {
		return ResponseEntity.ok(
                new TodoItemDTO(
                        1L,
                        todoItem.description(),
                        "not done",
                        Instant.now(),
                        todoItem.dueAtInstant(),
                        null,
                        Instant.now()

                )
        );
	}

}
