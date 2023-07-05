package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.PageRequestUtil;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    BookingDto changeStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                            @PathVariable("bookingId") long bookingId,
                            @RequestParam boolean approved) {
        return bookingService.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    List<BookingDto> getAllBookingsByBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(required = false) String state,
                                            @RequestParam(required = false) Long from,
                                            @RequestParam(required = false) Long size) {
        Pageable pageable = PageRequestUtil.createPageRequest(from, size);
        return bookingService.getAllBookingsByBooker(userId, state, pageable);
    }

    @GetMapping("/owner")
    List<BookingDto> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                           @RequestParam(required = false) String state,
                                           @RequestParam(required = false) Long from,
                                           @RequestParam(required = false) Long size) {
        Pageable pageable = PageRequestUtil.createPageRequest(from, size);
        return bookingService.getAllBookingsByOwner(ownerId, state, pageable);
    }

}
