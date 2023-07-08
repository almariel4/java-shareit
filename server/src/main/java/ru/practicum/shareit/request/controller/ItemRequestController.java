package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.PageRequestUtil;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private ItemRequestService itemRequestService;

    @PostMapping
    ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    List<ItemRequestDto> getOwnItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getAllWithPagination(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam (required = false) Integer from,
                                              @RequestParam (required = false) Integer size) {
        Pageable pageable = PageRequestUtil.createPageRequest(from, size);
        return itemRequestService.getAllWithPagination(userId, pageable);
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long requestId) {
        return itemRequestService.getItemRequest(userId, requestId);
    }
}
