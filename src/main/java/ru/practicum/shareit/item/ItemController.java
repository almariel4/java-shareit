package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public Item addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item editItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.editItem(userId, itemId, itemDto);
    }

    @GetMapping
    public List<Item> getItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByUser(userId);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<Item> searchForItems(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text) {
        return itemService.searchForItems(userId, text);
    }

}
