package ru.practicum.shareit.item.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    ItemService itemService;
    @InjectMocks
    ItemController itemController;

    private MockMvc mockMvc;
    private User user;
    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        user = new User(1L, "Anna", "test@test.ru");
        itemDto = new ItemDto(1L, "Качели", "Качели для малышей", true, 1L, null, null, new ArrayList<>());
        commentDto = new CommentDto(1L, "Отличные качели", itemDto.getId(), user.getName(),
                LocalDateTime.of(2023, 5, 30, 12, 0));
    }

    @SneakyThrows
    @Test
    void addItem() {

        when(itemService.addItem(anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(post("/items", itemDto)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect((jsonPath("$.available", is(itemDto.getAvailable()))))
                .andExpect((jsonPath("$.requestId", is(itemDto.getRequestId()))))
                .andExpect((jsonPath("$.lastBooking", is(itemDto.getLastBooking()))))
                .andExpect((jsonPath("$.nextBooking", is(itemDto.getNextBooking()))))
                .andExpect((jsonPath("$.comments", is(itemDto.getComments()))));

        verify(itemService, times(1)).addItem(user.getId(), itemDto);
    }

    @SneakyThrows
    @Test
    void editItem() {

        itemDto.setDescription("Updated description");
        itemDto.setAvailable(false);

        when(itemService.editItem(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(patch("/{itemId}", user.getId(), itemDto.getId(), itemDto)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect((jsonPath("$.available", is(itemDto.getAvailable()))))
                .andExpect((jsonPath("$.requestId", is(itemDto.getRequestId()))))
                .andExpect((jsonPath("$.lastBooking", is(itemDto.getLastBooking()))))
                .andExpect((jsonPath("$.nextBooking", is(itemDto.getNextBooking()))))
                .andExpect((jsonPath("$.comments", is(itemDto.getComments()))));

        verify(itemService, times(1)).addItem(user.getId(), itemDto);
    }

    @SneakyThrows
    @Test
    void getItemsByUser() {

        when(itemService.getItemsByUser(anyLong(), anyLong(), anyLong())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items", user.getId(), 0L, 20L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect((jsonPath("$[0].available", is(itemDto.getAvailable()))))
                .andExpect((jsonPath("$[0].requestId", is(itemDto.getRequestId()))))
                .andExpect((jsonPath("$[0].lastBooking", is(itemDto.getLastBooking()))))
                .andExpect((jsonPath("$[0].nextBooking", is(itemDto.getNextBooking()))))
                .andExpect((jsonPath("$[0].comments", is(itemDto.getComments()))));
    }

    @SneakyThrows
    @Test
    void getItem() {

        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/{itemId}", user.getId(), itemDto.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect((jsonPath("$.available", is(itemDto.getAvailable()))))
                .andExpect((jsonPath("$.requestId", is(itemDto.getRequestId()))))
                .andExpect((jsonPath("$.lastBooking", is(itemDto.getLastBooking()))))
                .andExpect((jsonPath("$.nextBooking", is(itemDto.getNextBooking()))))
                .andExpect((jsonPath("$.comments", is(itemDto.getComments()))));
    }

    @SneakyThrows
    @Test
    void searchForItems() {

        when(itemService.searchForItems(anyLong(), anyString(), anyLong(), anyLong())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/search", user.getId(), "Качели", 0L, 20L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect((jsonPath("$[0].available", is(itemDto.getAvailable()))))
                .andExpect((jsonPath("$[0].requestId", is(itemDto.getRequestId()))))
                .andExpect((jsonPath("$[0].lastBooking", is(itemDto.getLastBooking()))))
                .andExpect((jsonPath("$[0].nextBooking", is(itemDto.getNextBooking()))))
                .andExpect((jsonPath("$[0].comments", is(itemDto.getComments()))));
    }

    @SneakyThrows
    @Test
    void addComment() {

        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/{itemId}/comment", 1L, 1L, commentDto)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect((jsonPath("$.item", is(commentDto.getItem()))))
                .andExpect((jsonPath("$.authorName", is(commentDto.getAuthorName()))))
                .andExpect((jsonPath("$.created", is(commentDto.getCreated()))));

        verify(itemService, times(1)).addComment(user.getId(), itemDto.getId(), commentDto);
    }
}