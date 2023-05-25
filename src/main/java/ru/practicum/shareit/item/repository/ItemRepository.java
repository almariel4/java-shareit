package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item addItem(Long userId, ItemDto itemDto);

    Item editItem(Long userId, Long itemId, ItemDto itemDto);

    List<Item> getItemsByUser(Long userId);

    Item getItem(Long userId, Long itemId);

    List<Item> searchForItems(Long userId, String text);

}
