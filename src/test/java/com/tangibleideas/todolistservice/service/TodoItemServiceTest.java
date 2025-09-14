package com.tangibleideas.todolistservice.service;

import com.tangibleideas.todolistservice.api.dto.AddTodoItemRequest;
import com.tangibleideas.todolistservice.api.dto.TodoItemDTO;
import com.tangibleideas.todolistservice.api.dto.UpdateTodoItemRequest;
import com.tangibleideas.todolistservice.domain.TodoItem;
import com.tangibleideas.todolistservice.domain.TodoItemStatus;
import com.tangibleideas.todolistservice.mapper.TodoItemMapper;
import com.tangibleideas.todolistservice.repository.TodoItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoItemServiceTest {

    @Mock
    private TodoItemRepository todoItemRepository;

    @Mock
    private TodoItemMapper todoItemMapper;

    @InjectMocks
    private TodoItemService todoItemService;

    private final Instant FIXED_NOW_INSTANT = Instant.now();
    private final int SECONDS_DELAY_1 = 180;
    private TodoItem todoItem_Id1;
    private TodoItemDTO todoItemDTO;
    private AddTodoItemRequest addTodoItemRequest;

    @BeforeEach
    void setUp() {
        todoItem_Id1 = new TodoItem();
        todoItem_Id1.setId(1L);
        todoItem_Id1.setDescription("Test Description");
        todoItem_Id1.setStatus(TodoItemStatus.NOT_DONE);
        todoItem_Id1.setCreatedAt(FIXED_NOW_INSTANT);
        todoItem_Id1.setUpdatedAt(FIXED_NOW_INSTANT);
        todoItem_Id1.setDueAt(FIXED_NOW_INSTANT.plusSeconds(SECONDS_DELAY_1));
        todoItem_Id1.setDoneAt(null);

        todoItemDTO        = new TodoItemDTO(1L,
                                             "Test Description",
                                             TodoItemStatus.NOT_DONE,
                                             FIXED_NOW_INSTANT,
                                             FIXED_NOW_INSTANT.plusSeconds(
                                                     SECONDS_DELAY_1),
                                             null,
                                             FIXED_NOW_INSTANT);
        addTodoItemRequest = new AddTodoItemRequest("Test Description",
                                                    FIXED_NOW_INSTANT.plusSeconds(
                                                            SECONDS_DELAY_1));
    }

    @Nested
    class FindTodoItemByIdTests {
        @Test
        void shouldFindTodoItemById() {
            when(todoItemRepository.findById(1L)).thenReturn(Optional.of(
                    todoItem_Id1));
            when(todoItemMapper.toDTO(todoItem_Id1)).thenReturn(todoItemDTO);

            Optional<TodoItemDTO> result = todoItemService.findItemById(1L);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(todoItemDTO);
            verify(todoItemRepository, times(1)).findById(1L);
            verify(todoItemMapper, times(1)).toDTO(todoItem_Id1);
        }

        @Test
        void shouldNotFindTodoItemById() {
            when(todoItemRepository.findById(2L)).thenReturn(Optional.empty());

            Optional<TodoItemDTO> result = todoItemService.findItemById(2L);

            assertThat(result).isNotPresent();
            verify(todoItemRepository, times(1)).findById(2L);
            verify(todoItemMapper, never()).toDTO(todoItem_Id1);
        }
    }

    @Nested
    class AddTodoItemTests {
        @Test
        void shouldAddItemSuccessfully() {
            when(todoItemMapper.fromAddTodoItemRequest(addTodoItemRequest)).thenReturn(
                    todoItem_Id1);
            when(todoItemRepository.save(todoItem_Id1)).thenReturn(todoItem_Id1);
            when(todoItemMapper.toDTO(todoItem_Id1)).thenReturn(todoItemDTO);

            TodoItemDTO result = todoItemService.addItem(addTodoItemRequest);

            assertThat(result).isEqualTo(todoItemDTO);
            verify(todoItemMapper, times(1)).fromAddTodoItemRequest(
                    addTodoItemRequest);
            verify(todoItemRepository, times(1)).save(todoItem_Id1);
            verify(todoItemMapper, times(1)).toDTO(todoItem_Id1);
        }
    }

    @Nested
    class UpdateItemTests {
        @Test
        void shouldUpdateDescription() {
            String newDescription = "Updated Description";
            UpdateTodoItemRequest request = new UpdateTodoItemRequest(
                    newDescription,
                    null);

            Instant timeWhenUpdateHappened = Instant.now();

            TodoItem updatedTodoItem = TodoItem
                    .builder()
                    .id(todoItem_Id1.getId())
                    .description(newDescription)
                    .status(todoItem_Id1.getStatus())
                    .updatedAt(timeWhenUpdateHappened)
                    .createdAt(todoItem_Id1.getCreatedAt())
                    .doneAt(todoItem_Id1.getDoneAt())
                    .dueAt(todoItem_Id1.getDueAt())
                    .build();


            TodoItemDTO updatedDto = new TodoItemDTO(1L,
                                                     newDescription,
                                                     todoItemDTO.status(),
                                                     todoItemDTO.createdAt(),
                                                     todoItemDTO.dueAt(),
                                                     todoItem_Id1.getDoneAt(),
                                                     timeWhenUpdateHappened);

            // MOCK
            when(todoItemRepository.findById(1L)).thenReturn(Optional.of(
                    todoItem_Id1));
            when(todoItemRepository.save(any(TodoItem.class))).thenReturn(
                    updatedTodoItem);
            when(todoItemMapper.toDTO(updatedTodoItem)).thenReturn(updatedDto);

            Optional<TodoItemDTO> result = todoItemService.updateItem(1L,
                                                                      request);
            // ASSERT
            assertThat(result).isPresent();
            assertThat(result
                               .get()
                               .description()).isEqualTo(newDescription);
            verify(todoItemRepository, times(1)).save(any(TodoItem.class));
        }

        @Test
        void shouldUpdateStatusToDone() {
            UpdateTodoItemRequest request = new UpdateTodoItemRequest(null,
                                                                      TodoItemStatus.DONE);
            Instant timeWhenUpdateHappened = Instant.now();

            TodoItem updatedTodoItem = TodoItem
                    .builder()
                    .id(todoItem_Id1.getId())
                    .description(todoItem_Id1.getDescription())
                    .status(TodoItemStatus.DONE)
                    .updatedAt(timeWhenUpdateHappened)
                    .createdAt(todoItem_Id1.getCreatedAt())
                    .doneAt(todoItem_Id1.getDoneAt())
                    .dueAt(todoItem_Id1.getDueAt())
                    .build();


            TodoItemDTO updatedDto = new TodoItemDTO(1L,
                                                     todoItemDTO.description(),
                                                     TodoItemStatus.DONE,
                                                     todoItemDTO.createdAt(),
                                                     todoItemDTO.dueAt(),
                                                     timeWhenUpdateHappened,
                                                     timeWhenUpdateHappened);

            when(todoItemRepository.findById(1L)).thenReturn(Optional.of(
                    todoItem_Id1));
            when(todoItemRepository.save(any(TodoItem.class))).thenReturn(
                    updatedTodoItem);
            when(todoItemMapper.toDTO(any(TodoItem.class))).thenReturn(
                    updatedDto);

            Optional<TodoItemDTO> result = todoItemService.updateItem(1L,
                                                                      request);

            assertThat(result).isPresent();
            assertThat(result
                               .get()
                               .status()).isEqualTo(TodoItemStatus.DONE);
            assertThat(result
                               .get()
                               .doneAt()).isNotNull();
            verify(todoItemRepository, times(1)).save(any(TodoItem.class));
        }

        @Test
        void shouldUpdateStatusToNotDone() {
            UpdateTodoItemRequest request = new UpdateTodoItemRequest(null,
                                                                      TodoItemStatus.NOT_DONE);
            Instant timeWhenUpdateHappened = Instant.now();

            TodoItem initialTodoItem = TodoItem
                    .builder()
                    .id(todoItem_Id1.getId())
                    .description(todoItem_Id1.getDescription())
                    .status(TodoItemStatus.DONE)
                    .updatedAt(todoItem_Id1.getUpdatedAt())
                    .createdAt(todoItem_Id1.getCreatedAt())
                    .doneAt(todoItem_Id1.getDoneAt())
                    .dueAt(todoItem_Id1.getDueAt())
                    .build();

            TodoItem updatedTodoItem = TodoItem
                    .builder()
                    .id(initialTodoItem.getId())
                    .description(initialTodoItem.getDescription())
                    .status(TodoItemStatus.NOT_DONE)
                    .updatedAt(timeWhenUpdateHappened)
                    .createdAt(initialTodoItem.getCreatedAt())
                    .doneAt(initialTodoItem.getDoneAt())
                    .dueAt(initialTodoItem.getDueAt())
                    .build();


            TodoItemDTO updatedDto = new TodoItemDTO(1L,
                                                     todoItemDTO.description(),
                                                     TodoItemStatus.NOT_DONE,
                                                     todoItemDTO.createdAt(),
                                                     todoItemDTO.dueAt(),
                                                     null,
                                                     timeWhenUpdateHappened);

            when(todoItemRepository.findById(1L)).thenReturn(Optional.of(
                    initialTodoItem));
            when(todoItemRepository.save(any(TodoItem.class))).thenReturn(
                    updatedTodoItem);
            when(todoItemMapper.toDTO(any(TodoItem.class))).thenReturn(
                    updatedDto);

            Optional<TodoItemDTO> result = todoItemService.updateItem(1L,
                                                                      request);

            assertThat(result).isPresent();
            assertThat(result
                               .get()
                               .status()).isEqualTo(TodoItemStatus.NOT_DONE);
            assertThat(result
                               .get()
                               .doneAt()).isNull();
            verify(todoItemRepository, times(1)).save(any(TodoItem.class));
        }

        @Test
        void shouldNotSaveIfNoChanges() {
            UpdateTodoItemRequest request = new UpdateTodoItemRequest(
                    "Test Description",
                    TodoItemStatus.NOT_DONE);
            TodoItemDTO existingDto = todoItemDTO;

            when(todoItemRepository.findById(1L)).thenReturn(Optional.of(
                    todoItem_Id1));
            when(todoItemMapper.toDTO(any(TodoItem.class))).thenReturn(
                    existingDto);

            Optional<TodoItemDTO> result = todoItemService.updateItem(1L,
                                                                      request);

            assertThat(result).isPresent();
            assertThat(result
                               .get()
                               .description()).isEqualTo("Test Description");
            assertThat(result
                               .get()
                               .status()).isEqualTo(TodoItemStatus.NOT_DONE);
            verify(todoItemRepository, never()).save(any(TodoItem.class));
        }

        @Test
        void shouldReturnEmptyOptionalWhenItemToUpdateNotFound() {
            UpdateTodoItemRequest request = new UpdateTodoItemRequest(
                    "New Description",
                    null);
            when(todoItemRepository.findById(99L)).thenReturn(Optional.empty());

            Optional<TodoItemDTO> result = todoItemService.updateItem(99L,
                                                                      request);

            assertThat(result).isNotPresent();
            verify(todoItemRepository, never()).save(any(TodoItem.class));
        }
    }


    @Nested
    class GetAllItemsTests {
        @Test
        void shouldReturnAllItems() {
            List<TodoItem> items = List.of(todoItem_Id1);
            List<TodoItemDTO> dtos = List.of(todoItemDTO);
            when(todoItemRepository.findAll()).thenReturn(items);
            when(todoItemMapper.toDTOs(items)).thenReturn(dtos);

            List<TodoItemDTO> result = todoItemService.getAllItems();

            assertThat(result).isEqualTo(dtos);
            verify(todoItemRepository, times(1)).findAll();
            verify(todoItemMapper, times(1)).toDTOs(items);
        }
    }

    @Nested
    class GetNotDoneItemsTests {
        @Test
        void shouldReturnNotDoneItems() {
            List<TodoItem> notDoneItems = List.of(todoItem_Id1);
            List<TodoItemDTO> notDoneDtos = List.of(todoItemDTO);
            when(todoItemRepository.findAllByStatus(TodoItemStatus.NOT_DONE)).thenReturn(
                    notDoneItems);
            when(todoItemMapper.toDTOs(notDoneItems)).thenReturn(notDoneDtos);

            List<TodoItemDTO> result = todoItemService.getNotDoneItems();

            assertThat(result).isEqualTo(notDoneDtos);
            verify(todoItemRepository,
                   times(1)).findAllByStatus(TodoItemStatus.NOT_DONE);
            verify(todoItemMapper, times(1)).toDTOs(notDoneItems);
        }
    }
}