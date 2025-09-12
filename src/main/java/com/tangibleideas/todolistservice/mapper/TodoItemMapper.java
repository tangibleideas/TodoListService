package com.tangibleideas.todolistservice.mapper;

import com.tangibleideas.todolistservice.dto.AddTodoItemRequestDTO;
import com.tangibleideas.todolistservice.dto.TodoItemDTO;
import com.tangibleideas.todolistservice.model.TodoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TodoItemMapper {

	@Mapping(target = "createdAtInstant", source = "createdAt")
	@Mapping(target = "dueAtInstant", source = "dueAt")
	@Mapping(target = "doneAtInstant", source = "doneAt")
	@Mapping(target = "updatedAtInstant", ignore = true)
	TodoItemDTO toDTO(TodoItem todoItem);

	@Mapping(target = "createdAt", source = "createdAtInstant")
	@Mapping(target = "dueAt", source = "dueAtInstant")
	@Mapping(target = "doneAt", source = "doneAtInstant")
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "id", ignore = true)
	TodoItem toEntity(TodoItemDTO todoItemDTO);

	List<TodoItemDTO> toDTOs(List<TodoItem> todoItems);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
	@Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
	@Mapping(target = "status", constant = "not done")
	@Mapping(target = "doneAt", ignore = true)
	@Mapping(target = "dueAt", source = "dueAtInstant")
	TodoItem fromCreateRequest(AddTodoItemRequestDTO request);

}