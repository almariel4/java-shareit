package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto editItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> searchForItems(Long userId, String text);

    List<ItemDto> getItemsByUser(Long userId);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
