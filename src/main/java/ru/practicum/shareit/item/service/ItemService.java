package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemService {

    Item addItem(Long userId, ItemDto item);

    Item editItem(Long userId, Long itemId, ItemDto itemDto);

    Item getItem(Long userId, Long itemId);

    List<Item> searchForItems(Long userId, String text);

    List<Item> getItemsByUser(Long userId);
}
