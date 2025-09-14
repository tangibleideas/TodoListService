package com.tangibleideas.todolistservice.service;

import com.tangibleideas.todolistservice.api.dto.AddTodoItemRequest;
import com.tangibleideas.todolistservice.api.dto.TodoItemDTO;
import com.tangibleideas.todolistservice.api.dto.UpdateTodoItemRequest;
import com.tangibleideas.todolistservice.domain.TodoItem;
import com.tangibleideas.todolistservice.domain.TodoItemId;
import com.tangibleideas.todolistservice.domain.TodoItemStatus;
import com.tangibleideas.todolistservice.exception.StatusUpdateNotAllowedException;
import com.tangibleideas.todolistservice.mapper.TodoItemMapper;
import com.tangibleideas.todolistservice.repository.TodoItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * 3. Only save to database if there are updates and updates are allowed
     */
    @Transactional
    public Optional<TodoItemDTO> updateItem(
            Long id,
            UpdateTodoItemRequest request) {
        return todoItemRepository
                .findById(id)
                .map(existingTodoItem -> {
                    boolean isUpdated = false;

                    if (isDescriptionUpdateRequired(request,
                                                    existingTodoItem)) {
                        existingTodoItem.setDescription(request.description());
                        isUpdated = true;
                    }

                    if (isStatusUpdateRequired(request, existingTodoItem)) {
                        rejectStatusUpdateIfNeeded(existingTodoItem);
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

    private static boolean isDescriptionUpdateRequired(
            UpdateTodoItemRequest request,
            TodoItem existingTodoItem) {
        return request.description() != null &&
               !Objects.equals(request.description(),
                               existingTodoItem.getDescription());
    }

    private static boolean isStatusUpdateRequired(
            UpdateTodoItemRequest request,
            TodoItem existingTodoItem) {
        return request.status() != null &&
               !Objects.equals(request.status(),
                               existingTodoItem.getStatus());
    }

    /**
     * Raise exception if status is past due and status update requested
     *
     */
    private void rejectStatusUpdateIfNeeded(TodoItem existingTodoItem) {
        if (existingTodoItem
                .getStatus()
                .equals(TodoItemStatus.PAST_DUE)) {
            throw new StatusUpdateNotAllowedException(
                    "status update of past due item " +
                    existingTodoItem.getId() + " is not allowed");
        }
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

    public List<Long> getPastDueTodoItemIds() {
        return todoItemRepository
                .findByStatusAndDueAtBefore(
                        TodoItemStatus.NOT_DONE,
                        Instant.now())
                .stream()
                .map(TodoItemId::getId)
                .collect(Collectors.toList());

    }

    @Transactional
    public void setPastDueIfNotDone(Long id) {
        int result = todoItemRepository.updateStatusWithStatusCondition(id,
                                                                        TodoItemStatus.PAST_DUE,
                                                                        TodoItemStatus.NOT_DONE);
        if (result < 1) {
            log.warn(
                    "TodoItem with id {} not marked as PAST_DUE as current " +
                    "status not NOT_DONE",
                    id);
        }

    }
}
