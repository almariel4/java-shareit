package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable("itemId") long itemId,
                                           @Valid @RequestBody ItemDto itemDto) {
        return itemClient.editItem(userId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return itemClient.getItemsByUser(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable("itemId") long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchForItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam String text,
                                                 @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return itemClient.searchForItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("itemId") long itemId,
                                             @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
