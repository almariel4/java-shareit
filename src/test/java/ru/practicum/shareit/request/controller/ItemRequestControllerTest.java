package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;
    private ItemRequestDto itemRequestDto;
    String header = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        User user = new User(1L, "Anna", "test@test.ru");
        itemRequestDto = new ItemRequestDto(1L, "Требуются качели для малышей", user, LocalDateTime.of(2023, 5, 23, 12, 0), new ArrayList<>());
    }

    @SneakyThrows
    @Test
    void addItemRequest_whenItemRequestDtoIsValidated_thenReturnCreatedItemRequest() {
        when(itemRequestService.addItemRequest(anyLong(), any())).thenReturn(itemRequestDto);

        String response = mockMvc.perform(post("/requests")
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), response);
        verify(itemRequestService, times(1)).addItemRequest(anyLong(),any());
    }

    @SneakyThrows
    @Test
    void getOwnItemRequests_WhenItemRequestsExist_thenReturnListOfItemRequests() {
        when(itemRequestService.getOwnItemRequests(anyLong())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header(header, 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requestor.id", is(itemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$[0].requestor.name", is(itemRequestDto.getRequestor().getName())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].items", hasSize(0)));
    }

    @SneakyThrows
    @Test
    void getAllWithPagination() {
        when(itemRequestService.getAllWithPagination(anyLong(), any())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header(header, 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requestor.id", is(itemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$[0].requestor.name", is(itemRequestDto.getRequestor().getName())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].items", hasSize(0)));
    }

    @SneakyThrows
    @Test
    void getItemRequest() {
        when(itemRequestService.getItemRequest(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header(header, 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(itemRequestDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(itemRequestDto.getRequestor().getName())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }
}