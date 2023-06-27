package ru.practicum.shareit.request.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    ItemRequestService itemRequestService;
    @InjectMocks
    ItemRequestController itemRequestController;

    private MockMvc mockMvc;
    private ItemRequestDto itemRequestDto;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        user = new User(1L, "Anna", "test@test.ru");
        itemRequestDto = new ItemRequestDto(1L, "Требуются качели для малышей", user, LocalDateTime.of(2023, 5, 23, 12, 0), new ArrayList<>());
    }

    @SneakyThrows
    @Test
    void addItemRequest_whenItemRequestDtoIsValidated_thenReturnCreatedItemRequest() {

        when(itemRequestService.addItemRequest(anyLong(), any())).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is(itemRequestDto.getRequestor())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated())))
                .andExpect(jsonPath("$[0].items", is(itemRequestDto.getItems())));

        verify(itemRequestService, times(1)).addItemRequest(any(),itemRequestDto);
    }

    @SneakyThrows
    @Test
    void getOwnItemRequests_WhenItemRequestsExist_thenReturnListOfItemRequests() {

        when(itemRequestService.getOwnItemRequests(anyLong())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is(itemRequestDto.getRequestor())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated())))
                .andExpect(jsonPath("$[0].items", is(itemRequestDto.getItems())));
    }

    @SneakyThrows
    @Test
    void getAllWithPagination() {

        when(itemRequestService.getAllWithPagination(anyLong(), 0L, 20L)).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is(itemRequestDto.getRequestor())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated())))
                .andExpect(jsonPath("$[0].items", is(itemRequestDto.getItems())));
    }

    @SneakyThrows
    @Test
    void getItemRequest() {

        when(itemRequestService.getItemRequest(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(itemRequestDto.getRequestor())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated())))
                .andExpect(jsonPath("$.items", is(itemRequestDto.getItems())));
    }
}