package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    Pageable pageable;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Anna", "test@test.ru");
        itemRequestDto = new ItemRequestDto(1L, "Требуются качели для малышей", user, LocalDateTime.of(2023, 5, 23, 12, 0), new ArrayList<>());
        itemRequest = new ItemRequest(1L, "Требуются качели для малышей", user, LocalDateTime.of(2023, 5, 23, 12, 0));
        item = new Item(1L, "Качели", "Качели для малышей", true, 2L, 1L);
        itemDto = new ItemDto(1L, "Качели", "Качели для малышей", true, 1L, null, null, new ArrayList<>());
    }

    @Test
    void addItemRequest_whenUserNotFound_thenThrowNotFoundException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.addItemRequest(user.getId(), itemRequestDto);
        });
        assertEquals("Пользователь с id = " + user.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void addItemRequest_whenDescriptionNull_thenThrowBadRequestException() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        itemRequestDto.setDescription(null);

        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            itemRequestService.addItemRequest(user.getId(), itemRequestDto);
        });
        assertEquals("Описание запроса не должно быть пустым", thrown.getMessage());
    }

    @Test
    void addItemRequest_whenItemRequestIsValidated_thenReturnItemRequestDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto itemRequestDtoTest = itemRequestService.addItemRequest(user.getId(), itemRequestDto);

        assertEquals(itemRequestDto.getId(), itemRequestDtoTest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequestDtoTest.getDescription());
        assertEquals(itemRequestDto.getRequestor(), itemRequestDtoTest.getRequestor());
        assertEquals(itemRequestDto.getCreated(), itemRequestDtoTest.getCreated());
    }

    @Test
    void getOwnItemRequests_whenUserNotFound_thenThrowNotFoundException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.getOwnItemRequests(user.getId());
        });
        assertEquals("Пользователь с id = " + user.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void getOwnItemRequests_whenUserExists_thenReturnedListOfItemDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository.getAllByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(Optional.of(List.of(itemRequest)));

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getOwnItemRequests(user.getId());

        assertEquals(itemRequestDto.getId(), itemRequestDtos.get(0).getId());
        assertEquals(itemRequestDto.getDescription(), itemRequestDtos.get(0).getDescription());
        assertEquals(itemRequestDto.getRequestor(), itemRequestDtos.get(0).getRequestor());
        assertEquals(itemRequestDto.getCreated(), itemRequestDtos.get(0).getCreated());
        assertEquals(itemRequestDto.getItems().size(), itemRequestDtos.get(0).getItems().size());
    }

    @Test
    void getAllWithPagination_whenUserNotFound_thenThrowNotFoundException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.getAllWithPagination(user.getId(), null);
        });
        assertEquals("Пользователь с id = " + user.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void getItemRequest_whenUserNotFound_thenThrowNotFoundException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.getItemRequest(user.getId(), itemRequest.getId());
        });
        assertEquals("Пользователь с id = " + user.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void getItemRequest_whenItemRequestNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.getItemRequest(user.getId(), itemRequest.getId());
        });
        assertEquals("Запрос с id = " + itemRequest.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void getItemRequest_whenItemRequestExistsWithItems_thenReturnedItemRequestDtoWithItems() {
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.getItemByRequestId(anyLong())).thenReturn(Optional.of(item));

        itemRequestDto.setItems(List.of(itemDto));
        itemRequestRepository.save(itemRequest);

        ItemRequestDto itemRequestDtoTest = itemRequestService.getItemRequest(user.getId(), itemRequest.getId());

        assertEquals(itemRequestDto.getId(), itemRequestDtoTest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequestDtoTest.getDescription());
        assertEquals(itemRequestDto.getRequestor(), itemRequestDtoTest.getRequestor());
        assertEquals(itemRequestDto.getCreated(), itemRequestDtoTest.getCreated());
        assertEquals(itemRequestDto.getItems().size(), itemRequestDtoTest.getItems().size());
    }

    @Test
    void addItemToRequest_whenItemRequestExistsWithItems_thenReturnedListOfItemRequestDtoWithItems() {
        when(itemRepository.getItemByRequestId(itemRequest.getId())).thenReturn(Optional.of(item));

        List<ItemRequestDto> itemRequestDtos = itemRequestService.addItemToRequest(List.of(itemRequest));

        assertEquals(1, itemRequestDtos.size());
        assertEquals(item.getId(), itemRequestDtos.get(0).getItems().get(0).getId());
        assertEquals(item.getName(), itemRequestDtos.get(0).getItems().get(0).getName());
    }
}