package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Anna", "test@test.ru");
        item = new Item(1L, "Качели", "Качели для малышей", true, 2L, 1L);
        itemDto = new ItemDto(1L, "Качели", "Качели для малышей", true, 1L, null, null, new ArrayList<>());
        commentDto = new CommentDto(1L, "Отличные качели", itemDto.getId(), user.getName(),
                LocalDateTime.of(2023, 5, 30, 12, 0));
        booking = new Booking(1L,
                LocalDateTime.of(2023, 5, 30, 12, 0),
                LocalDateTime.of(2023, 7, 30, 12, 0),
                item, user, user.getId(), BookingStatus.WAITING);
    }

    @Test
    void addItem_whenItemNameBlank_thenThrownBadRequestException() {
        itemDto.setName("");

        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            itemService.addItem(user.getId(), itemDto);
        });

        assertEquals("Не заполнены необходимые поля новой вещи", thrown.getMessage());
    }

    @Test
    void addItem_whenItemDescriptionNull_thenThrownBadRequestException() {
        itemDto.setDescription(null);

        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            itemService.addItem(user.getId(), itemDto);
        });

        assertEquals("Не заполнены необходимые поля новой вещи", thrown.getMessage());
    }

    @Test
    void addItem_whenItemAvailableNull_thenThrownBadRequestException() {
        itemDto.setAvailable(null);

        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            itemService.addItem(user.getId(), itemDto);
        });

        assertEquals("Не заполнены необходимые поля новой вещи", thrown.getMessage());
    }

    @Test
    void addItem_whenUserNotFound_thenThrownNotFoundException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.addItem(user.getId(), itemDto);
        });

        assertEquals("Пользователь с id = " + user.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void addItem_whenItemIsValidated_thenReturnItemDto() {
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any())).thenReturn(item);

        userRepository.save(user);
        ItemDto itemDtoTest = itemService.addItem(user.getId(), itemDto);

        assertEquals(1L, itemDtoTest.getId());
        assertEquals("Качели", itemDtoTest.getName());
        assertEquals("Качели для малышей", itemDtoTest.getDescription());
        assertEquals(true, itemDtoTest.getAvailable());
        assertEquals(1L, itemDtoTest.getRequestId());
        assertNull(itemDtoTest.getLastBooking());
        assertNull(itemDtoTest.getNextBooking());
    }

    @Test
    void editItem_whenUserNotFound_thenThrowNotFoundException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.editItem(user.getId(), item.getId(), itemDto);
        });

        assertEquals("Пользователь с id = " + user.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void editItem_whenItemNotFound_thenThrowNotFoundException() {
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        userRepository.save(user);
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.editItem(user.getId(), item.getId(), itemDto);
        });

        assertEquals("Вещь с id = " + item.getId() + " не найдена", thrown.getMessage());
    }

    @Test
    void editItem_whenItemIsValidated_thenReturnItemDto() {
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any())).thenReturn(item);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        userRepository.save(user);
        itemRepository.save(item);
        itemDto.setName("Updated Name");
        itemDto.setDescription("Updated Description");
        itemDto.setAvailable(false);
        ItemDto itemDtoTest = itemService.editItem(user.getId(), item.getId(), itemDto);

        assertEquals(1L, itemDtoTest.getId());
        assertEquals("Updated Name", itemDtoTest.getName());
        assertEquals("Updated Description", itemDtoTest.getDescription());
        assertEquals(false, itemDtoTest.getAvailable());
        assertEquals(1L, itemDtoTest.getRequestId());
        assertNull(itemDtoTest.getLastBooking());
        assertNull(itemDtoTest.getNextBooking());
    }

    @Test
    void getItemsByUser_whenUserNotFound_thenThrowNotFoundException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.getItemsByUser(user.getId(), PageRequest.of(0, 20));
        });

        assertEquals("Пользователь с id = " + user.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void getItemsByUser_whenPageRequestNotNull_thenReturnListOfItemDto() {
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any())).thenReturn(item);
        when(itemRepository.findItemsByOwnerOrderById(anyLong(), any(PageRequest.class))).thenReturn(List.of(item));

        userRepository.save(user);
        itemRepository.save(item);
        List<ItemDto> items = itemService.getItemsByUser(user.getId(), PageRequest.of(0, 20));

        assertEquals(1L, items.get(0).getId());
        assertEquals("Качели", items.get(0).getName());
        assertEquals("Качели для малышей", items.get(0).getDescription());
        assertEquals(true, items.get(0).getAvailable());
        assertEquals(1L, items.get(0).getRequestId());
        assertNull(items.get(0).getLastBooking());
        assertNull(items.get(0).getNextBooking());
    }

    @Test
    void getItemsByUser_whenPageRequestNull_thenReturnListOfItemDto() {
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any())).thenReturn(item);
        when(itemRepository.findItemsByOwnerOrderById(anyLong())).thenReturn(List.of(item));

        userRepository.save(user);
        itemRepository.save(item);
        List<ItemDto> items = itemService.getItemsByUser(user.getId(), null);

        assertEquals(1L, items.get(0).getId());
        assertEquals("Качели", items.get(0).getName());
        assertEquals("Качели для малышей", items.get(0).getDescription());
        assertEquals(true, items.get(0).getAvailable());
        assertEquals(1L, items.get(0).getRequestId());
        assertNull(items.get(0).getLastBooking());
        assertNull(items.get(0).getNextBooking());
    }

    @Test
    void setBookings_whenBookingsExist_thenReturnedItemDtoWithBookings() {
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.getLastBooking(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.getNextBooking(anyLong())).thenReturn(Optional.ofNullable(booking));

        Booking lastBooking = booking;
        Booking nextBooking = booking;

        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));
        nextBooking.setStart(LocalDateTime.now().plusDays(250));
        nextBooking.setEnd(LocalDateTime.now().plusDays(255));
        bookingRepository.save(lastBooking);
        bookingRepository.save(nextBooking);
        user.setId(2L);

        ItemDto itemDtoTest = itemService.setBookings(user.getId(), item);

        assertEquals(lastBooking, itemDtoTest.getLastBooking());
        assertEquals(nextBooking, itemDtoTest.getNextBooking());
    }

    @Test
    void getItem_whenUserNotFound_thenThrowNotFoundException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.getItem(user.getId(), item.getId());
        });

        assertEquals("Пользователь с id = " + user.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void getItem_whenItemNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.getItem(user.getId(), item.getId());
        });

        assertEquals("Вещь с id = " + item.getId() + " не найдена", thrown.getMessage());
    }

    @Test
    void getItem_whenItemIsValidated_thenReturnItemDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        ItemDto itemDtoTest = itemService.getItem(user.getId(), item.getId());

        assertEquals(1L, itemDtoTest.getId());
        assertEquals("Качели", itemDtoTest.getName());
        assertEquals("Качели для малышей", itemDtoTest.getDescription());
        assertEquals(true, itemDtoTest.getAvailable());
        assertEquals(1L, itemDtoTest.getRequestId());
        assertNull(itemDtoTest.getLastBooking());
        assertNull(itemDtoTest.getNextBooking());
    }

    @Test
    void searchForItems_whenPageRequestIsNull_thenReturnListOfItemDto() {
        when(itemRepository.search(anyString())).thenReturn(List.of(item));

        List<ItemDto> itemDtos = itemService.searchForItems(user.getId(), "Качели", PageRequest.of(0, 20));

        assertEquals(1, itemDtos.size());
        assertEquals(1L, itemDtos.get(0).getId());
    }

    @Test
    void addComment_whenCommentTextBlank_thenThrowBadRequestException() {
        commentDto.setText("");
        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            itemService.addComment(user.getId(), item.getId(), commentDto);
        });

        assertEquals("Текст комментария не может быть пустым", thrown.getMessage());
    }

    @Test
    void addComment_whenUserNotFound_thenThrowNotFoundException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.addComment(user.getId(), item.getId(), commentDto);
        });

        assertEquals("Пользователь с id = " + user.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void addComment_whenItemNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.addComment(user.getId(), item.getId(), commentDto);
        });

        assertEquals("Вещь с id = " + item.getId() + " не найдена", thrown.getMessage());
    }

    @Test
    void addComment_whenItemNotBooked_thenThrowBadRequestException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            itemService.addComment(user.getId(), item.getId(), commentDto);
        });

        assertEquals("Пользователь не может оставить комментарий к вещи", thrown.getMessage());
    }
}