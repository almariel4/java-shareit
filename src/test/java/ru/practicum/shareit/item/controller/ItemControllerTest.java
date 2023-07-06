package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mockMvc;

    private User user;
    private ItemDto itemDto;
    private CommentDto commentDto;
    String header = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        user = new User(1L, "Anna", "test@test.ru");
        itemDto = new ItemDto(1L, "Качели", "Качели для малышей", true, 1L, null, null, new ArrayList<>());
        commentDto = new CommentDto(1L, "Отличные качели", itemDto.getId(), user.getName(),
                LocalDateTime.of(2023, 5, 30, 12, 0));
    }

    @SneakyThrows
    @Test
    void addItem_whenItemIsValidated_ThenReturnItemDto() {
        when(itemService.addItem(anyLong(), any())).thenReturn(itemDto);

        String response = mockMvc.perform(post("/items")
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(itemDto), response);
        verify(itemService, times(1)).addItem(user.getId(), itemDto);
    }

    @SneakyThrows
    @Test
    void editItem_whenItemIsValidated_ThenReturnUpdatedItemDto() {

        itemDto.setDescription("Updated description");
        itemDto.setAvailable(false);

        when(itemService.editItem(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect((jsonPath("$.available", is(itemDto.getAvailable()))))
                .andExpect((jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class)))
                .andExpect((jsonPath("$.lastBooking", is(itemDto.getLastBooking()))))
                .andExpect((jsonPath("$.nextBooking", is(itemDto.getNextBooking()))));

        verify(itemService, times(1)).editItem(anyLong(), anyLong(), any());
    }

    @SneakyThrows
    @Test
    void getItemsByUser_whenUserIsValidated_thenReturnListOfItems() {
        when(itemService.getItemsByUser(anyLong(), any())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header(header, 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect((jsonPath("$[0].available", is(itemDto.getAvailable()))))
                .andExpect((jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class)))
                .andExpect((jsonPath("$[0].comments", is(itemDto.getComments()))));
    }

    @SneakyThrows
    @Test
    void getItem_whenItemExists_thenReturnItemDto() {
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header(header, 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect((jsonPath("$.available", is(itemDto.getAvailable()))))
                .andExpect((jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class)))
                .andExpect((jsonPath("$.lastBooking", is(itemDto.getLastBooking()))))
                .andExpect((jsonPath("$.nextBooking", is(itemDto.getNextBooking()))))
                .andExpect((jsonPath("$.comments", is(itemDto.getComments()))));
    }

    @SneakyThrows
    @Test
    void searchForItems_whenSearchTextIsNotBlank_ThenReturnListOfItems() {
        when(itemService.searchForItems(anyLong(), anyString(), any())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header(header, 1L)
                        .param("text", "Качели")
                        .param("from", "0")
                        .param("size", "20")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect((jsonPath("$[0].available", is(itemDto.getAvailable()))))
                .andExpect((jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class)))
                .andExpect((jsonPath("$[0].lastBooking", is(itemDto.getLastBooking()))))
                .andExpect((jsonPath("$[0].nextBooking", is(itemDto.getNextBooking()))))
                .andExpect((jsonPath("$[0].comments", is(itemDto.getComments()))));
    }

    @SneakyThrows
    @Test
    void addComment_whenCommentIsValidated_thenReturnCommentDto() {
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        String response = mockMvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(commentDto), response);
        verify(itemService, times(1)).addComment(user.getId(), itemDto.getId(), commentDto);
    }
}