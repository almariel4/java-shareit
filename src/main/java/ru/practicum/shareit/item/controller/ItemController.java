package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.PageRequestUtil;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader("X-Sharer-User-Id") long userId,
                            @PathVariable("itemId") Long itemId,
                            @Valid @RequestBody ItemDto itemDto) {
        return itemService.editItem(userId, itemId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam(required = false) Long from,
                                        @RequestParam(required = false) Long size) {
        Pageable pageable = PageRequestUtil.createPageRequest(from, size);
        return itemService.getItemsByUser(userId, pageable);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable("itemId") long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchForItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam String text,
                                        @RequestParam(required = false) Long from,
                                        @RequestParam(required = false) Long size) {
        Pageable pageable = PageRequestUtil.createPageRequest(from, size);
        return itemService.searchForItems(userId, text, pageable);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable("itemId") Long itemId,
                                 @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
