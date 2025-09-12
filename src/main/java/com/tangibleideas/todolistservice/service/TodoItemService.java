package com.tangibleideas.todolistservice.service;

import com.tangibleideas.todolistservice.dto.AddTodoItemRequestDTO;
import com.tangibleideas.todolistservice.dto.TodoItemDTO;
import com.tangibleideas.todolistservice.mapper.TodoItemMapper;
import com.tangibleideas.todolistservice.model.TodoItem;
import com.tangibleideas.todolistservice.repository.TodoItemRepository;
import org.springframework.stereotype.Service;

@Service
public class TodoItemService {

	private final TodoItemRepository todoItemRepository;

	private final TodoItemMapper todoItemMapper;

	public TodoItemService(TodoItemRepository todoItemRepository, TodoItemMapper todoItemMapper) {
		this.todoItemRepository = todoItemRepository;
		this.todoItemMapper = todoItemMapper;
	}

	public TodoItemDTO findItemById(Long id) {
		TodoItem todoItem = todoItemRepository.findById(id).orElse(null);
		return todoItem != null ? todoItemMapper.toDTO(todoItem) : null;
	}

	public TodoItemDTO addItem(AddTodoItemRequestDTO request) {
		TodoItem todoItem = todoItemMapper.fromCreateRequest(request);
		TodoItem saved = todoItemRepository.save(todoItem);
		return todoItemMapper.toDTO(saved);
	}

}
