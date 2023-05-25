package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private int item;
    private int booker;
    private BookingStatus status;
}
