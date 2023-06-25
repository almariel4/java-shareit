package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequestDto;

import java.util.List;


public interface ItemRequestService {

    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getOwnItemRequests(Long userId);

    List<ItemRequestDto> getAllWithPagination(Long userId, Long from, Long size);

    ItemRequestDto getItemRequest(Long userId, Long itemRequestId);
}
