package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

//@Data
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BookingDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Item item;
    private User booker;
    private Long bookerId;
    private BookingStatus status;
}
