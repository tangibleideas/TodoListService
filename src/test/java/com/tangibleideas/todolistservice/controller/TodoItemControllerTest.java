package com.tangibleideas.todolistservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tangibleideas.todolistservice.api.controller.TodoItemController;
import com.tangibleideas.todolistservice.api.dto.AddTodoItemRequest;
import com.tangibleideas.todolistservice.api.dto.TodoItemDTO;
import com.tangibleideas.todolistservice.api.dto.UpdateTodoItemRequest;
import com.tangibleideas.todolistservice.domain.TodoItemStatus;
import com.tangibleideas.todolistservice.service.TodoItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.tangibleideas.todolistservice.util.Constants.MAX_DESCRIPTION_LENGTH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoItemController.class)
class TodoItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditorAware<String> auditorAware;

    @MockitoBean
    private TodoItemService todoItemService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(
            new JavaTimeModule());

    private final String API_V1_TODOS = "/api/v1/todos";
    private final Instant FIXED_NOW_INSTANT = Instant.now();
    private final int SECONDS_DELAY_1 = 180;
    private final String DESCRIPTION_TODO_ITEM_1 = "Todo Item with ID 1";


    private final TodoItemDTO todoItemDTO_Id1 = new TodoItemDTO(1L,
                                                                DESCRIPTION_TODO_ITEM_1,
                                                                TodoItemStatus.NOT_DONE,
                                                                FIXED_NOW_INSTANT,
                                                                FIXED_NOW_INSTANT.plusSeconds(
                                                                        SECONDS_DELAY_1),
                                                                null,
                                                                FIXED_NOW_INSTANT);


    @Test
    void whenGetTodoItemById_thenReturnTodoItem() throws Exception {
        when(todoItemService.findItemById(1L)).thenReturn(Optional.of(
                todoItemDTO_Id1));

        mockMvc
                .perform(MockMvcRequestBuilders
                                 .get(API_V1_TODOS + "/1")
                                 .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_ID).value(1))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_DESCRIPTION).value(
                        DESCRIPTION_TODO_ITEM_1))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_STATUS).value(
                        TodoItemStatus.NOT_DONE.getLabel()))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_CREATED).value(
                        FIXED_NOW_INSTANT.toString()))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_DUE).value(
                        FIXED_NOW_INSTANT
                                .plusSeconds(SECONDS_DELAY_1)
                                .toString()));
    }

    @Test
    void whenGetTodoItemByIdNotFound_thenReturn404() throws Exception {
        when(todoItemService.findItemById(1L)).thenReturn(Optional.empty());

        mockMvc
                .perform(MockMvcRequestBuilders
                                 .get(API_V1_TODOS + "/1")
                                 .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void whenGetTodoItemByIdWithWrongIdFormat_thenReturn400() throws Exception {

        mockMvc
                .perform(MockMvcRequestBuilders
                                 .get(API_V1_TODOS + "/abc")
                                 .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void whenAddTodoItem_thenReturnCreated() throws Exception {
        when(todoItemService.addItem(any())).thenReturn(todoItemDTO_Id1);

        AddTodoItemRequest addTodoItemRequest = new AddTodoItemRequest(
                DESCRIPTION_TODO_ITEM_1,
                FIXED_NOW_INSTANT.plusSeconds(SECONDS_DELAY_1));

        mockMvc
                .perform(MockMvcRequestBuilders
                                 .post(API_V1_TODOS)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(
                                         addTodoItemRequest)))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_ID).value(1))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_DESCRIPTION).value(
                        DESCRIPTION_TODO_ITEM_1))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_STATUS).value(
                        TodoItemStatus.NOT_DONE.getLabel()))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_CREATED).value(
                        FIXED_NOW_INSTANT.toString()))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_DUE).value(
                        FIXED_NOW_INSTANT
                                .plusSeconds(SECONDS_DELAY_1)
                                .toString()));
    }

    @Test
    void whenAddTodoItemWithInvalidRequestBody_thenReturn400()
            throws Exception {
        when(todoItemService.addItem(any())).thenReturn(todoItemDTO_Id1);

        String invalidJsonRequest = "I am not a JSON string";

        mockMvc
                .perform(MockMvcRequestBuilders
                                 .post(API_V1_TODOS)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(invalidJsonRequest))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void whenAddTodoItemWithTooLongDescription_thenReturn400()
            throws Exception {
        when(todoItemService.addItem(any())).thenReturn(todoItemDTO_Id1);

        String tooLongDescription = "x".repeat(MAX_DESCRIPTION_LENGTH + 1);
        AddTodoItemRequest addTodoItemRequest = new AddTodoItemRequest(
                tooLongDescription,
                null);

        mockMvc
                .perform(MockMvcRequestBuilders
                                 .post(API_V1_TODOS)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(
                                         addTodoItemRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void whenAddTodoItemWithWrongDueDateFormat_thenReturn400()
            throws Exception {
        when(todoItemService.addItem(any())).thenReturn(todoItemDTO_Id1);


        Map<String, String> requestBodyMap = new HashMap<>() {{
            put("description", DESCRIPTION_TODO_ITEM_1);
            put("due_datetime", "BAD DATE");
        }};


        mockMvc
                .perform(MockMvcRequestBuilders
                                 .post(API_V1_TODOS)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(
                                         requestBodyMap)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void whenUpdateTodoItemDescription_thenReturnUpdated() throws Exception {
        when(todoItemService.updateItem(any(), any())).thenReturn(Optional.of(
                todoItemDTO_Id1));

        mockMvc
                .perform(MockMvcRequestBuilders
                                 .patch(API_V1_TODOS + "/1")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(
                                         new UpdateTodoItemRequest(
                                                 DESCRIPTION_TODO_ITEM_1,
                                                 null))))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_ID).value(1))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_DESCRIPTION).value(
                        DESCRIPTION_TODO_ITEM_1))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_STATUS).value(
                        TodoItemStatus.NOT_DONE.getLabel()))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_CREATED).value(
                        FIXED_NOW_INSTANT.toString()))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_DUE).value(
                        FIXED_NOW_INSTANT
                                .plusSeconds(SECONDS_DELAY_1)
                                .toString()));

    }

    @Test
    void whenUpdateTodoItemStatus_thenReturnUpdated() throws Exception {
        when(todoItemService.updateItem(any(), any())).thenReturn(Optional.of(
                todoItemDTO_Id1));

        mockMvc
                .perform(MockMvcRequestBuilders
                                 .patch(API_V1_TODOS + "/1")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(
                                         new UpdateTodoItemRequest(
                                                 null,
                                                 TodoItemStatus.NOT_DONE))))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_ID).value(1))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_DESCRIPTION).value(
                        DESCRIPTION_TODO_ITEM_1))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_STATUS).value(
                        TodoItemStatus.NOT_DONE.getLabel()))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_CREATED).value(
                        FIXED_NOW_INSTANT.toString()))
                .andExpect(jsonPath(Constants.JsonPaths.TODO_ITEM_DUE).value(
                        FIXED_NOW_INSTANT
                                .plusSeconds(SECONDS_DELAY_1)
                                .toString()));

    }

    @Test
    void whenUpdateTodoItemNotFound_thenReturn404() throws Exception {
        when(todoItemService.updateItem(any(),
                                        any())).thenReturn(Optional.empty());

        mockMvc
                .perform(MockMvcRequestBuilders
                                 .get(API_V1_TODOS + "/1")
                                 .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }


    @Test
    void whenGetTodoItemsNotDone_thenReturnListByCallingGetNotDoneItems()
            throws Exception {

        when(todoItemService.getAllItems()).thenReturn(List.of(todoItemDTO_Id1));
        mockMvc
                .perform(MockMvcRequestBuilders
                                 .get(API_V1_TODOS)
                                 .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$").isArray());

        verify(todoItemService, times(1)).getNotDoneItems();
    }

    @Test
    void whenGetTodoItemsAll_thenReturnListByCallingGetAllItems()
            throws Exception {
        when(todoItemService.getAllItems()).thenReturn(List.of(todoItemDTO_Id1));
        mockMvc
                .perform(MockMvcRequestBuilders
                                 .get(API_V1_TODOS + "?all=true")
                                 .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$").isArray());

        verify(todoItemService, times(1)).getAllItems();
    }


}