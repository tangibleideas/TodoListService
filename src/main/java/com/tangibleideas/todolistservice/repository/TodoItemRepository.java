package com.tangibleideas.todolistservice.repository;

import com.tangibleideas.todolistservice.domain.TodoItem;
import com.tangibleideas.todolistservice.domain.TodoItemId;
import com.tangibleideas.todolistservice.domain.TodoItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

    List<TodoItem> findAllByStatus(TodoItemStatus status);

    List<TodoItemId> findByStatusAndDueAtBefore(TodoItemStatus status, Instant dueAt);

    @Modifying
    @Query("UPDATE TodoItem t SET t.status = :newStatus WHERE t.id = :id AND t.status = :currentStatus")
    int updateStatusWithStatusCondition(Long id, TodoItemStatus newStatus, TodoItemStatus currentStatus);
}
