package ru.practicum.shareit.booking.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    BookingService bookingService;
    @InjectMocks
    BookingController bookingController;

    MockMvc mockMvc;
    BookingDto bookingDto;
    Item item;
    User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        user = new User(1L, "Anna", "test@test.ru");
        item = new Item(1L, "Качели", "Качели для малышей", true, 1L, 1L);
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

        mockMvc.perform(post("/bookings", 1L, bookingDto)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd())))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId())))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker())))
                .andExpect(jsonPath("$.bookerId", is(bookingDto.getBookerId())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())));

        verify(bookingService, times(1)).addBooking(user.getId(), bookingDto);
    }

    @SneakyThrows
    @Test
    void changeStatus_whenBookingExists_ThenReturnBookingDtoWithChangedStatus() {

        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L, 1L, true)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd())))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId())))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker())))
                .andExpect(jsonPath("$.bookerId", is(bookingDto.getBookerId())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())));
    }

    @SneakyThrows
    @Test
    void getBooking_whenBookingExists_thenReturnBookingDto() {

        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L, 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd())))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId())))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker())))
                .andExpect(jsonPath("$.bookerId", is(bookingDto.getBookerId())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus())));
    }

    @SneakyThrows
    @Test
    void getAllBookingsByBooker() {

        when(bookingService.getAllBookingsByBooker(anyLong(), anyString(), anyLong(), anyLong())).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings", 1, "ALL", 0, 20)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd())))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId())))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker())))
                .andExpect(jsonPath("$[0].bookerId", is(bookingDto.getBookerId())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus())));
    }

    @SneakyThrows
    @Test
    void getAllBookingsByOwner() {

        when(bookingService.getAllBookingsByOwner(anyLong(), anyString(), anyLong(), anyLong())).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings", 1, "ALL", 0, 20)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd())))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId())))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem())))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker())))
                .andExpect(jsonPath("$[0].bookerId", is(bookingDto.getBookerId())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus())));
    }
}