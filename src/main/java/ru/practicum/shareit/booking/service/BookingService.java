package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(Long userId, BookingDto bookingDto);

    BookingDto changeStatus(Long userId, Long bookingId, boolean approved);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getAllBookingsByBooker(Long userId, String state, Pageable pageable);

    List<BookingDto> getAllBookingsByOwner(Long userId, String state, Pageable pageable);
}
