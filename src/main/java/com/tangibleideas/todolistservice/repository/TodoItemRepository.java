package com.tangibleideas.todolistservice.repository;

import com.tangibleideas.todolistservice.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

}
