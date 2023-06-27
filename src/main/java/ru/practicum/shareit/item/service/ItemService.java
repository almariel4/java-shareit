package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto editItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> searchForItems(Long userId, String text, Long from, Long size);

    List<ItemDto> getItemsByUser(Long userId, Long from, Long size);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
