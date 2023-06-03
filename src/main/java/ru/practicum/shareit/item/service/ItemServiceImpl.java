package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public Item addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(userId, itemDto);
        return itemRepository.addItem(item);
    }

    @Override
    public Item editItem(Long userId, Long itemId, ItemDto itemDto) {
        return itemRepository.editItem(userId, itemId, itemDto);
    }

    @Override
    public List<Item> getItemsByUser(Long userId) {
        return itemRepository.getItemsByUser(userId);
    }

    @Override
    public Item getItem(Long userId, Long itemId) {
        return itemRepository.getItem(userId, itemId);
    }

    @Override
    public List<Item> searchForItems(Long userId, String text) {
        return itemRepository.searchForItems(userId, text);
    }

}
