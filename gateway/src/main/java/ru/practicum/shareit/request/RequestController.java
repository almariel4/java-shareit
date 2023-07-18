package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return requestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    ResponseEntity<Object> getOwnItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestClient.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getAllWithPagination(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                @Positive @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return requestClient.getAllWithPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long requestId) {
        return requestClient.getItemRequest(userId, requestId);
    }
}
