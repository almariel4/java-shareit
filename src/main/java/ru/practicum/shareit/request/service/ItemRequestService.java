package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequestDto;

import java.util.List;


public interface ItemRequestService {

    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getOwnItemRequests(Long userId);

    List<ItemRequestDto> getAllWithPagination(Long userId, Pageable pageable);

    ItemRequestDto getItemRequest(Long userId, Long itemRequestId);
}
