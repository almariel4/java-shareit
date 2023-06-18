package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;


public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(null)
                .build();
    }

    public static Item mapToItem(Long userId, ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(userId);
        return item;
    }

}
