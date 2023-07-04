package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        if (itemRequestDto.getDescription() == null) {
            throw new BadRequestException("Описание запроса не должно быть пустым");
        }
        itemRequestDto.setRequestor(user);
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.mapToItemRequest(itemRequestDto));
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getOwnItemRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Optional<List<ItemRequest>> itemRequests = itemRequestRepository.getAllByRequestorIdOrderByCreatedDesc(userId);
        List<ItemRequest> itemRequestToAddItem = new ArrayList<>();
        itemRequests.ifPresent(itemRequestToAddItem::addAll);
        return addItemToRequest(itemRequestToAddItem);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAllWithPagination(Long userId, Long from, Long size) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        if (from != null || size != null) {
            if (from < 0 || size < 0) {
                throw new BadRequestException("Индекс первого элемента и количество элементов не могут быть отрицательными");
            }
            if (from == 0 && size == 0) {
                throw new BadRequestException("Нечего возвращать");
            }
            int pageNumber = (int) (from / size);
            Pageable pageable = PageRequest.of(pageNumber, Math.toIntExact(size));
            List<ItemRequest> itemRequests = itemRequestRepository.findAll(pageable).stream()
                    .filter(itemRequest -> !itemRequest.getRequestor().getId().equals(userId))
                    .collect(Collectors.toList());
            itemRequestDtos = addItemToRequest(itemRequests);
        }
        return itemRequestDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getItemRequest(Long userId, Long itemRequestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(() ->
                new NotFoundException("Запрос с id = " + itemRequestId + " не найден"));
        Optional<Item> item = itemRepository.getItemByRequestId(itemRequest.getId());
        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);
        item.ifPresent(item1 -> itemRequestDto.setItems(List.of(ItemMapper.mapToItemDto(item1))));
        return itemRequestDto;
    }

//    Сделан public для юнит-тестирования
    public List<ItemRequestDto> addItemToRequest(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            Optional<Item> item = itemRepository.getItemByRequestId(itemRequest.getId());
            if (item.isPresent()) {
                ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);
                itemRequestDto.setItems(List.of(ItemMapper.mapToItemDto(item.get())));
                itemRequestDtos.add(itemRequestDto);
            } else {
                ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);
                itemRequestDto.setItems(new ArrayList<>());
                itemRequestDtos.add(itemRequestDto);
            }
        }
        return itemRequestDtos;
    }
}
