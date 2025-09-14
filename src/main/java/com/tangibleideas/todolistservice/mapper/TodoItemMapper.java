package com.tangibleideas.todolistservice.mapper;

import com.tangibleideas.todolistservice.api.dto.AddTodoItemRequest;
import com.tangibleideas.todolistservice.api.dto.TodoItemDTO;
import com.tangibleideas.todolistservice.domain.TodoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TodoItemMapper {


    @Mapping(target = "updatedAt", ignore = true)
    TodoItemDTO toDTO(TodoItem todoItem);


    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    TodoItem toEntity(TodoItemDTO todoItemDTO);

    List<TodoItemDTO> toDTOs(List<TodoItem> todoItems);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "NOT_DONE")
    @Mapping(target = "doneAt",  ignore = true)
    @Mapping(target = "createdAt",  ignore = true)
    @Mapping(target = "updatedAt",  ignore = true)
    TodoItem fromAddTodoItemRequest(AddTodoItemRequest request);

}