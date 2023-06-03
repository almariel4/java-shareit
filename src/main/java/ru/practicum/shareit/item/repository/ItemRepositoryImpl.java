package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.MissingValueException;
import ru.practicum.shareit.user.repository.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private Map<Long, Item> items = new HashMap<>();
    private Map<Long, List<Long>> itemsByUsers = new HashMap<>();
    private static Long itemId = 1L;
    private final UserRepository userRepository;

    public ItemRepositoryImpl(UserRepositoryImpl userRepositoryImpl) {
        this.userRepository = userRepositoryImpl;
    }

    @Override
    public Item addItem(Item item) {
        if (userRepository.getAllUsers().stream().noneMatch(user -> user.getId().equals(item.getOwner()))) {
            throw new NotFoundException("Пользователь с id " + item.getOwner() + "не найден");
        }
        if (item.getName().isBlank()) {
            throw new MissingValueException("Имя не может быть пустым");
        }
        if (item.getAvailable() == null) {
            throw new MissingValueException("Должен быть указан статус доступности");
        }
        if (item.getDescription() == null) {
            throw new MissingValueException("Описание не может быть пустым");
        }
        item.setId(generateId());
        items.put(item.getId(), item);
        List<Long> itemIds;
        if (itemsByUsers.containsKey(item.getOwner())) {
            itemIds = itemsByUsers.get(item.getOwner());
            itemIds.add(item.getId());
        } else {
            itemIds = new ArrayList<>();
            itemIds.add(item.getId());
        }
        itemsByUsers.put(item.getOwner(), itemIds);
        return item;
    }

    @Override
    public Item editItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = items.get(itemId);
        if (item != null && item.getOwner().equals(userId)) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            items.remove(itemId);
            items.put(itemId, item);
        } else {
            throw new NotFoundException("Вещь с id " + itemId + "не найдена");
        }
        return item;
    }

    @Override
    public List<Item> getItemsByUser(Long userId) {
        List<Long> itemsByUser = itemsByUsers.get(userId);
        List<Item> itemList = items.values().stream().filter(item -> itemsByUser.contains(item.getId())).collect(Collectors.toList());
        return itemList;
    }

    @Override
    public Item getItem(Long userId, Long itemId) {
        Item item = items.get(itemId);
        if (item != null) {
            return item;
        } else {
            throw new NotFoundException("Вещь с id " + itemId + "не найдена");
        }
    }

    @Override
    public List<Item> searchForItems(Long userId, String text) {
        List<Item> itemsFound = new ArrayList<>();
        if (!text.isBlank()) {
            itemsFound = items.values().stream()
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        }
        return itemsFound;
    }

    private static Long generateId() {
        return itemId++;
    }
}
