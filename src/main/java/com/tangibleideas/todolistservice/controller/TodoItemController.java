package com.tangibleideas.todolistservice.controller;

import com.tangibleideas.todolistservice.dto.AddTodoItemRequestDTO;
import com.tangibleideas.todolistservice.dto.TodoItemDTO;
import com.tangibleideas.todolistservice.service.TodoItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TodoItemController {

	private final TodoItemService todoItemService;

	public TodoItemController(TodoItemService todoItemService) {
		this.todoItemService = todoItemService;
	}

	@GetMapping(value = "/todos/{id}")
	public ResponseEntity<TodoItemDTO> getTodoItem(@PathVariable Long id) {
		return ResponseEntity.ok(todoItemService.findItemById(id));
	}

	@PostMapping(value = "/todos")
	public ResponseEntity<TodoItemDTO> addTodoItem(@RequestBody AddTodoItemRequestDTO todoItem) {
		return ResponseEntity.ok(todoItemService.addItem(todoItem));
	}

}
