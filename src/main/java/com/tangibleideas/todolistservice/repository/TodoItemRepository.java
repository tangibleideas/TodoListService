package com.tangibleideas.todolistservice.repository;

import com.tangibleideas.todolistservice.domain.TodoItem;
import com.tangibleideas.todolistservice.domain.TodoItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

    List<TodoItem> findAllByStatus(TodoItemStatus status);
}
