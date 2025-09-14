package com.tangibleideas.todolistservice.service;

import com.tangibleideas.todolistservice.api.dto.AddTodoItemRequest;
import com.tangibleideas.todolistservice.api.dto.TodoItemDTO;
import com.tangibleideas.todolistservice.api.dto.UpdateTodoItemRequest;
import com.tangibleideas.todolistservice.domain.TodoItem;
import com.tangibleideas.todolistservice.domain.TodoItemStatus;
import com.tangibleideas.todolistservice.mapper.TodoItemMapper;
import com.tangibleideas.todolistservice.repository.TodoItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class TodoItemService {

    private final TodoItemRepository todoItemRepository;

    private final TodoItemMapper todoItemMapper;


    public TodoItemService(
            TodoItemRepository todoItemRepository,
            TodoItemMapper todoItemMapper) {
        this.todoItemRepository = todoItemRepository;
        this.todoItemMapper     = todoItemMapper;
    }

    public Optional<TodoItemDTO> findItemById(Long id) {
        return todoItemRepository
                .findById(id)
                .map(todoItemMapper::toDTO);
    }

    /**
     * Add TodoItem to database. No custom logic, simply map DTO to Entity
     * and save in the database using repository
     */
    public TodoItemDTO addItem(AddTodoItemRequest request) {
        TodoItem todoItem = todoItemMapper.fromAddTodoItemRequest(request);
        TodoItem saved = todoItemRepository.save(todoItem);
        return todoItemMapper.toDTO(saved);
    }

    /**
     * Update TodoItem based on request only if database object requires update
     * 1. Fetch item from database ->
     * 2. Check if there are any updates ->
     * 3. Only save to database if there are updates
     */
    @Transactional
    public Optional<TodoItemDTO> updateItem(
            Long id,
            UpdateTodoItemRequest request) {
        return todoItemRepository
                .findById(id)
                .map(existingTodoItem -> {
                    boolean isUpdated = false;

                    // Check and update description if different and not null
                    if (request.description() != null &&
                        !Objects.equals(request.description(),
                                        existingTodoItem.getDescription())) {
                        existingTodoItem.setDescription(request.description());
                        isUpdated = true;
                    }

                    // Check and update status if different and not null
                    if (request.status() != null &&
                        !Objects.equals(request.status(),
                                        existingTodoItem.getStatus())) {
                        setStatusWithLogic(existingTodoItem, request.status());
                        isUpdated = true;
                    }

                    if (isUpdated) {
                        TodoItem updatedTodoItem = todoItemRepository.save(
                                existingTodoItem);
                        return todoItemMapper.toDTO(updatedTodoItem);
                    } else {
                        log.debug(
                                "TodoItem with id {} already up to date, " +
                                "skipping database write.",
                                id);
                        return todoItemMapper.toDTO(existingTodoItem);
                    }
                });
    }

    public List<TodoItemDTO> getAllItems() {
        return todoItemMapper.toDTOs(todoItemRepository.findAll());
    }

    public List<TodoItemDTO> getNotDoneItems() {
        return todoItemMapper.toDTOs(todoItemRepository.findAllByStatus(
                TodoItemStatus.NOT_DONE));
    }

    /**
     * Logic connected with status changes.
     * 1. When status changes to done, also set doneAt.
     * 2. When status changes to not done, set doneAt to null.
     */
    private void setStatusWithLogic(TodoItem todoItem, TodoItemStatus status) {
        if (status.equals(TodoItemStatus.DONE)) {
            todoItem.setStatus(TodoItemStatus.DONE);
            todoItem.setDoneAt(Instant.now());
        }
        if (status.equals(TodoItemStatus.NOT_DONE)) {
            todoItem.setStatus(status);
            todoItem.setDoneAt(null);
        }
    }
}
