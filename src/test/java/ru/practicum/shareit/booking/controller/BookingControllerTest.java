package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    private BookingDto bookingDto;
    String header = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        User user = new User(1L, "Anna", "test@test.ru");
        Item item = new Item(1L, "Качели", "Качели для малышей", true, 1L, 1L);
        bookingDto = new BookingDto(1L,
                LocalDateTime.of(2023, 5, 30, 12, 0),
                LocalDateTime.of(2023, 7, 30, 12, 0),
                item.getId(), item, user, user.getId(), BookingStatus.WAITING
        );
    }

    @SneakyThrows
    @Test
    void addBooking_whenBookingIsValidated_thenReturnBookingDto() {
        when(bookingService.addBooking(anyLong(), any())).thenReturn(bookingDto);

        String response = mockMvc.perform(post("/bookings")
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(bookingDto), response);
        verify(bookingService, times(1)).addBooking(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void changeStatus_whenBookingExists_ThenReturnBookingDtoWithChangedStatus() {

        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(header, 1L)
                        .param("approved", String.valueOf(true))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.bookerId", is(bookingDto.getBookerId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));
    }

    @SneakyThrows
    @Test
    void getBooking_whenBookingExists_thenReturnBookingDto() {

        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(header, 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.bookerId", is(bookingDto.getBookerId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));
    }

    @SneakyThrows
    @Test
    void getAllBookingsByBooker() {
        when(bookingService.getAllBookingsByBooker(anyLong(), anyString(), anyLong(), anyLong())).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header(header, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].bookerId", is(bookingDto.getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().name())));
    }

    @SneakyThrows
    @Test
    void getAllBookingsByOwner() {
        when(bookingService.getAllBookingsByOwner(anyLong(), anyString(), anyLong(), anyLong())).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(header, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].bookerId", is(bookingDto.getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().name())));
    }
}