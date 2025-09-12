package com.tangibleideas.todolistservice.mapper;

import com.tangibleideas.todolistservice.dto.AddTodoItemRequestDTO;
import com.tangibleideas.todolistservice.dto.TodoItemDTO;
import com.tangibleideas.todolistservice.model.TodoItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TodoItemMapperTest {

	@Autowired
	private TodoItemMapper mapper;

	@Test
	void shouldMapTodoItemToDTO() {
		Instant now = Instant.now();
		Instant dueTime = now.plusSeconds(3600);

		TodoItem item = TodoItem.builder()
			.id(1L)
			.description("Test task")
			.status("not done")
			.createdAt(now)
			.updatedAt(now)
			.dueAt(dueTime)
			.build();

		TodoItemDTO dto = mapper.toDTO(item);

		assertThat(dto.id()).isEqualTo(1L);
		assertThat(dto.description()).isEqualTo("Test task");
		assertThat(dto.status()).isEqualTo("not done");
		assertThat(dto.createdAtInstant()).isEqualTo(now);
		assertThat(dto.dueAtInstant()).isEqualTo(dueTime);
		assertThat(dto.doneAtInstant()).isNull();
		assertThat(dto.updatedAtInstant()).isNull();
	}

	@Test
	void shouldMapCreateRequestToTodoItem() {
		Instant dueTime = Instant.now().plusSeconds(3600);

		AddTodoItemRequestDTO request = new AddTodoItemRequestDTO("New task", dueTime);

		TodoItem item = mapper.fromCreateRequest(request);

		assertThat(item.getId()).isNull();
		assertThat(item.getDescription()).isEqualTo("New task");
		assertThat(item.getStatus()).isEqualTo("not done");
		assertThat(item.getCreatedAt()).isNotNull();
		assertThat(item.getUpdatedAt()).isNotNull();
		assertThat(item.getDueAt()).isEqualTo(dueTime);
		assertThat(item.getDoneAt()).isNull();
	}

	@Test
	void shouldHandleNullValues() {
		TodoItemDTO dto = mapper.toDTO(null);
		assertThat(dto).isNull();

		TodoItem item = mapper.fromCreateRequest(null);
		assertThat(item).isNull();
	}

}