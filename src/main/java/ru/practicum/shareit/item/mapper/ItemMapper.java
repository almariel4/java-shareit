package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    static Long itemId = 1L;

    private static Long generateId() {
        return itemId++;
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(Long userId, ItemDto itemDto) {
        return new Item(
                generateId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId,
                null
        );
    }

}
