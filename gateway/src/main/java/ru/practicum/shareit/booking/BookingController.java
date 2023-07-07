package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable(name = "bookingId") long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    //	custom
    @PatchMapping("/{bookingId}")
    ResponseEntity<Object> changeStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable("bookingId") long bookingId,
                                        @RequestParam(name = "approved") Boolean approved) {
        return bookingClient.changeStatus(userId, bookingId, approved);
    }

    //    custom
    @GetMapping("/owner")
    ResponseEntity<Object> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                 @RequestParam(required = false, defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") @Min(0) Long from,
                                                 @Positive @RequestParam(value = "size", defaultValue = "10") @Min(1) Long size) {
        return bookingClient.getAllBookingsByOwner(ownerId, state, from, size);
    }
}
