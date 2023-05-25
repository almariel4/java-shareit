package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.MissingValueException;
import ru.practicum.shareit.user.repository.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private List<Item> items = new ArrayList<>();

    final UserRepositoryImpl userRepositoryImpl;

    public ItemRepositoryImpl(UserRepositoryImpl userRepositoryImpl) {
        this.userRepositoryImpl = userRepositoryImpl;
    }

    @Override
    public Item addItem(Long userId, ItemDto itemDto) {
        if (userRepositoryImpl.getAllUsers().stream().noneMatch(user -> user.getId().equals(userId))) {
            throw new NotFoundException("Пользователь с id " + userId + "не найден");
        }
        if (itemDto.getName().isBlank()) {
            throw new MissingValueException("Имя не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new MissingValueException("Должен быть указан статус доступности");
        }
        if (itemDto.getDescription() == null) {
            throw new MissingValueException("Описание не может быть пустым");
        }
        Item item = ItemMapper.toItem(userId, itemDto);
        items.add(item);
        return item;
    }

    @Override
    public Item editItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = items.stream().filter(x -> x.getId().equals(itemId)).findFirst().get();
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
            items.remove(item);
            items.add(item);
        } else {
            throw new NotFoundException("Вещь с id " + item.getId() + "не найдена");
        }
        return item;
    }

    @Override
    public List<Item> getItemsByUser(Long userId) {
        return items.stream().filter(item -> item.getOwner().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public Item getItem(Long userId, Long itemId) {
        Item item = items.stream().filter(item1 -> item1.getId().equals(itemId)).findFirst().get();
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
            itemsFound = items.stream()
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        }
        return itemsFound;
    }

}
