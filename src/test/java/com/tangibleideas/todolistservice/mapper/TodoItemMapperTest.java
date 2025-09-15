package com.tangibleideas.todolistservice.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.tangibleideas.todolistservice.api.dto.AddTodoItemRequest;
import com.tangibleideas.todolistservice.api.dto.TodoItemDTO;
import com.tangibleideas.todolistservice.domain.TodoItem;
import com.tangibleideas.todolistservice.domain.TodoItemStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TodoItemMapperTest {

  @Autowired private TodoItemMapper mapper;

  @Test
  void toDTO_shouldMapCorrectlyToTodoItemDTO() {
    Instant now = Instant.now();
    String description = "Description";
    Instant dueTime = now.plusSeconds(3600);
    TodoItem todoItem =
        TodoItem.builder()
            .id(1L)
            .description(description)
            .status(TodoItemStatus.NOT_DONE)
            .createdAt(now)
            .updatedAt(now)
            .dueAt(dueTime)
            .build();

    TodoItemDTO dto = mapper.toDTO(todoItem);

    assertThat(dto.id()).isEqualTo(1L);
    assertThat(dto.description()).isEqualTo(description);
    assertThat(dto.status()).isEqualTo(TodoItemStatus.NOT_DONE);
    assertThat(dto.createdAt()).isEqualTo(now);
    assertThat(dto.dueAt()).isEqualTo(dueTime);
    assertThat(dto.doneAt()).isNull();
    assertThat(dto.updatedAt()).isNull();
  }

  @Test
  void toDTOs_shouldMapCorrectlyToTodoItemDTOs() {
    Instant now = Instant.now();
    String firstTodoItemDescription = "First task description";
    String secondTodoItemDescription = "Second task description";

    TodoItem todoItem1 =
        TodoItem.builder()
            .id(1L)
            .description(firstTodoItemDescription)
            .status(TodoItemStatus.DONE)
            .createdAt(now)
            .updatedAt(now)
            .dueAt(now)
            .doneAt(now)
            .build();

    TodoItem todoItem2 =
        TodoItem.builder()
            .id(2L)
            .description(secondTodoItemDescription)
            .status(TodoItemStatus.NOT_DONE)
            .createdAt(now)
            .updatedAt(now)
            .dueAt(null)
            .doneAt(null)
            .build();

    List<TodoItemDTO> mappedDTOs = mapper.toDTOs(List.of(todoItem1, todoItem2));

    assertThat(mappedDTOs).isNotNull();
    assertThat(mappedDTOs).hasSize(2);

    TodoItemDTO firstDTO = mappedDTOs.get(0);
    assertThat(firstDTO.id()).isEqualTo(1L);
    assertThat(firstDTO.description()).isEqualTo(firstTodoItemDescription);
    assertThat(firstDTO.status()).isEqualTo(TodoItemStatus.DONE);
    assertThat(firstDTO.createdAt()).isEqualTo(now);
    assertThat(firstDTO.updatedAt()).isNull();
    assertThat(firstDTO.dueAt()).isEqualTo(now);
    assertThat(firstDTO.doneAt()).isEqualTo(now);

    TodoItemDTO secondDTO = mappedDTOs.get(1);
    assertThat(secondDTO.id()).isEqualTo(2L);
    assertThat(secondDTO.description()).isEqualTo(secondTodoItemDescription);
    assertThat(secondDTO.status()).isEqualTo(TodoItemStatus.NOT_DONE);
    assertThat(secondDTO.createdAt()).isEqualTo(now);
    assertThat(secondDTO.updatedAt()).isNull();
    assertThat(secondDTO.dueAt()).isNull();
    assertThat(secondDTO.doneAt()).isNull();
  }

  @Test
  void fromAddTodoItemRequest_shouldMapCorrectlyToTodoItem() {
    Instant dueTime = Instant.now().plusSeconds(3600);
    String description = "Description";

    AddTodoItemRequest request = new AddTodoItemRequest(description, dueTime);

    TodoItem item = mapper.fromAddTodoItemRequest(request);

    assertThat(item.getId()).isNull();
    assertThat(item.getDescription()).isEqualTo(description);
    assertThat(item.getStatus()).isEqualTo(TodoItemStatus.NOT_DONE);
    assertThat(item.getCreatedAt()).isNull();
    assertThat(item.getUpdatedAt()).isNull();
    assertThat(item.getDueAt()).isEqualTo(dueTime);
    assertThat(item.getDoneAt()).isNull();
  }

  @Test
  void shouldHandleNullValues() {
    TodoItemDTO dto = mapper.toDTO(null);
    assertThat(dto).isNull();

    TodoItem item = mapper.fromAddTodoItemRequest(null);
    assertThat(item).isNull();
  }
}
