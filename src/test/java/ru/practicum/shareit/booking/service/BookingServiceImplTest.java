package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingDto bookingDto;
    private Booking booking;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Anna", "test@test.ru");
        item = new Item(1L, "Качели", "Качели для малышей", true, 2L, 1L);

        bookingDto = new BookingDto(1L,
                LocalDateTime.of(2023, 5, 30, 12, 0),
                LocalDateTime.of(2023, 7, 30, 12, 0),
                item.getId(), item, user, user.getId(), BookingStatus.WAITING);

        booking = new Booking(1L,
                LocalDateTime.of(2023, 5, 30, 12, 0),
                LocalDateTime.of(2023, 7, 30, 12, 0),
                item, user, user.getId(), BookingStatus.WAITING);
    }

    @Test
    void addBooking_whenItemNotFound_thenThrownNotFoundException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(user.getId(), bookingDto);
        });

        assertEquals("Вещь с id = " + bookingDto.getItemId() + " не найдена", thrown.getMessage());
    }

    @Test
    void addBooking_whenUserNotFound_thenThrownNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(user.getId(), bookingDto);
        });

        assertEquals("Пользователь с id = " + user.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void addBooking_whenUserIdEqualsOwnerId_thenThrownNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        item.setOwner(user.getId());

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(user.getId(), bookingDto);
        });

        assertEquals("Владелец вещи не может создать на нее бронирование", thrown.getMessage());
    }

    @Test
    void addBooking_whenItemIsAnavailable_thenThrownBadRequestException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        item.setAvailable(false);

        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            bookingService.addBooking(user.getId(), bookingDto);
        });

        assertEquals("Вещь недоступна для бронирования", thrown.getMessage());
    }

    @Test
    void addBooking_whenStartIsNull_thenThrownBadRequestException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        bookingDto.setStart(null);

        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            bookingService.addBooking(user.getId(), bookingDto);
        });

        assertEquals("Должны быть заполнены дата начала и дата окончания бронирования", thrown.getMessage());
    }

    @Test
    void addBooking_whenEndIsNull_thenThrownBadRequestException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        bookingDto.setEnd(null);

        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            bookingService.addBooking(user.getId(), bookingDto);
        });

        assertEquals("Должны быть заполнены дата начала и дата окончания бронирования", thrown.getMessage());
    }

    @Test
    void addBooking_whenStartIsBeforeNow_thenThrownBadRequestException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        bookingDto.setStart(LocalDateTime.now().minusDays(2));

        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            bookingService.addBooking(user.getId(), bookingDto);
        });

        assertEquals("Дата начала бронирования не должна быть раньше текущей даты и времени", thrown.getMessage());
    }

    @Test
    void addBooking_whenStartIsAfterEnd_thenThrownBadRequestException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        bookingDto.setStart(bookingDto.getEnd().plusDays(2));

        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            bookingService.addBooking(user.getId(), bookingDto);
        });

        assertEquals("Дата окончания бронирования не должна быть позже даты начала", thrown.getMessage());
    }

    @Test
    void addBooking_whenBookingDtoIsValidated_thenReturnedBookingDto() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.save(any())).thenReturn(booking);

        bookingDto.setStart(LocalDateTime.now().plusMonths(2));
        bookingDto.setEnd(LocalDateTime.now().plusMonths(4));
        bookingDto.getItem().setId(item.getId());

        BookingDto bookingDtoTest = bookingService.addBooking(user.getId(), bookingDto);

        assertEquals(1L, bookingDtoTest.getId());
        assertEquals(user, bookingDtoTest.getBooker());
        assertEquals(user.getId(), bookingDtoTest.getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtoTest.getStatus());
    }

    @Test
    void changeStatus_whenBookingIdNotFound_thenThrownNotFoundException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.changeStatus(user.getId(), booking.getId(), true);
        });

        assertEquals("Бронирование с id = " + booking.getId() + " не найдено", thrown.getMessage());
    }

    @Test
    void changeStatus_whenUserNotOwner_thenThrownNotFoundException() {
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        bookingRepository.save(booking);

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.changeStatus(user.getId(), booking.getId(), true);
        });

        assertEquals("Пользователь не является владельцем вещи для бронирования", thrown.getMessage());
    }

    @Test
    void changeStatus_whenStatusAlreadyApproved_thenThrownBadRequestException() {
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        booking.getItem().setOwner(user.getId());
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            bookingService.changeStatus(user.getId(), booking.getId(), true);
        });

        assertEquals("Владелец вещи уже одобрил бронь вещи", thrown.getMessage());
    }

    @Test
    void changeStatus_whenParamsValidated_thenReturnedBookingDto() {
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        booking.getItem().setOwner(user.getId());
        bookingRepository.save(booking);
        BookingDto bookingDtoTest = bookingService.changeStatus(user.getId(), booking.getId(), true);

        assertEquals(1L, bookingDtoTest.getId());
        assertEquals(user, bookingDtoTest.getBooker());
        assertEquals(user.getId(), bookingDtoTest.getBookerId());
        assertEquals(BookingStatus.APPROVED, bookingDtoTest.getStatus());
    }

    @Test
    void getBooking_whenUserNotFound_thenThrowNotFoundException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBooking(user.getId(), booking.getId());
        });

        assertEquals("Пользователь с id = " + user.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void getBooking_whenBookingNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBooking(user.getId(), booking.getId());
        });

        assertEquals("Бронирование с id = " + booking.getId() + " не найдено", thrown.getMessage());
    }

    @Test
    void getBooking_whenBookerOrOwnerNotEqualUser_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBooking(23L, booking.getId());
        });

        assertEquals("Бронирование с id = " + booking.getId() + "у пользователя с id "
                + "не найдено", thrown.getMessage());
    }

    @Test
    void getBooking_whenBookingIsValidated_thenReturnedBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        BookingDto bookingDtoTest = bookingService.getBooking(user.getId(), booking.getId());

        assertEquals(1L, bookingDtoTest.getId());
        assertEquals(user, bookingDtoTest.getBooker());
        assertEquals(user.getId(), bookingDtoTest.getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtoTest.getStatus());
    }

    @Test
    void getAllBookingsByBooker_whenUserNotFound_thenThrowNotFoundException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getAllBookingsByBooker(user.getId(), null, null, null);
        });

        assertEquals("Пользователь с id = " + user.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void getAllBookingsByBooker_whenStateNull_thenReturnedListOfBookingDtoStateAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.getBookingsByBookerId_OrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));

        bookingRepository.save(booking);
        List<BookingDto> bookingDtos = bookingService.getAllBookingsByBooker(user.getId(), null, 0L, 20L);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByBooker_whenStateNullAndPageRequestNull_thenReturnedListOfBookingDtoStateAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.getBookingsByBookerId_OrderByStartDesc(anyLong())).thenReturn(List.of(booking));

        bookingRepository.save(booking);
        List<BookingDto> bookingDtos = bookingService.getAllBookingsByBooker(user.getId(), null, null, null);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByBooker_whenStateNullAndPageRequestNotNull_thenReturnedListOfBookingDtoStateAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.getBookingsByBookerId_OrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));

        bookingRepository.save(booking);
        List<BookingDto> bookingDtos = bookingService.getAllBookingsByBooker(user.getId(), null, 0L, 20L);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByBooker_whenStateCurrent_thenReturnedListOfBookingDtoStateCurrent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.getBookingsByBookerId_OrderByStart_Current(anyLong())).thenReturn(List.of(booking));

        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking);

        List<BookingDto> bookingDtos = bookingService.getAllBookingsByBooker(user.getId(), "CURRENT", 0L, 20L);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByBooker_whenStatePast_thenReturnedListOfBookingDtoStatePast() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.getBookingsByBookerId_OrderByStart_Past(anyLong())).thenReturn(List.of(booking));

        bookingDto.setStart(LocalDateTime.now().minusDays(2));
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking);

        List<BookingDto> bookingDtos = bookingService.getAllBookingsByBooker(user.getId(), "PAST", 0L, 20L);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByBooker_whenStateFuture_thenReturnedListOfBookingDtoStateFuture() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.getBookingsByBookerId_OrderByStart_Future(anyLong())).thenReturn(List.of(booking));

        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(3));
        bookingRepository.save(booking);

        List<BookingDto> bookingDtos = bookingService.getAllBookingsByBooker(user.getId(), "FUTURE", 0L, 20L);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByBooker_whenStatusWaiting_thenReturnedListOfBookingDtoStatusWaiting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.getBookingsByBookerId_OrderByBookerId(anyLong(), any())).thenReturn(List.of(booking));

        bookingRepository.save(booking);
        List<BookingDto> bookingDtos = bookingService.getAllBookingsByBooker(user.getId(), "WAITING", 0L, 20L);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByBooker_whenStatusRejected_thenReturnedListOfBookingDtoStatusRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.getBookingsByBookerId_OrderByBookerId(anyLong(), any())).thenReturn(List.of(booking));

        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        List<BookingDto> bookingDtos = bookingService.getAllBookingsByBooker(user.getId(), "REJECTED", 0L, 20L);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.REJECTED, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByBooker_whenUnsupportedStatus_thenUnsupportedStatusException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        UnsupportedStatusException thrown = Assertions.assertThrows(UnsupportedStatusException.class, () -> {
            bookingService.getAllBookingsByBooker(user.getId(), "UNSUPPORTED", null, null);
        });

        assertEquals("Unknown state: UNSUPPORTED_STATUS", thrown.getMessage());
    }

    @Test
    void getAllBookingsByOwner_whenUserNotFound_thenThrowNotFoundException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getAllBookingsByOwner(user.getId(), null, null, null);
        });

        assertEquals("Пользователь с id = " + user.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void getAllBookingsByOwner_whenStateNull_thenReturnedListOfBookingDtoStateAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.getBookingsByItemOwnerOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));

        bookingDto.getItem().setId(item.getId());
        bookingRepository.save(booking);
        List<BookingDto> bookingDtos = bookingService.getAllBookingsByOwner(user.getId(), null, 0L, 20L);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByOwner_whenStateNullAndPageRequestNull_thenReturnedListOfBookingDtoStateAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.getBookingsByItemOwnerOrderByStartDesc(anyLong())).thenReturn(List.of(booking));

        bookingDto.getItem().setId(item.getId());
        bookingRepository.save(booking);
        List<BookingDto> bookingDtos = bookingService.getAllBookingsByOwner(user.getId(), null, null, null);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByOwner_whenStateNullAndPageRequestNotNull_thenReturnedListOfBookingDtoStateAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.getBookingsByItemOwnerOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));

        bookingDto.getItem().setId(item.getId());
        bookingRepository.save(booking);
        List<BookingDto> bookingDtos = bookingService.getAllBookingsByOwner(user.getId(), null, 0L, 20L);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByOwner_whenStateCurrent_thenReturnedListOfBookingDtoStateCurrent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.getBookingsByOwnerAndStatus_Current(anyLong())).thenReturn(List.of(booking));

        bookingDto.setStart(LocalDateTime.now().minusMonths(4));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.getItem().setId(item.getId());
        bookingRepository.save(booking);
        List<BookingDto> bookingDtos = bookingService.getAllBookingsByOwner(user.getId(), "CURRENT", 0L, 20L);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByOwner_whenStatePast_thenReturnedListOfBookingDtoStatePast() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.getBookingsByOwnerAndStatus_Past(anyLong())).thenReturn(List.of(booking));

        bookingDto.setStart(LocalDateTime.now().minusMonths(4));
        bookingDto.setEnd(LocalDateTime.now().minusMonths(2));
        bookingDto.getItem().setId(item.getId());
        bookingRepository.save(booking);

        List<BookingDto> bookingDtos = bookingService.getAllBookingsByOwner(user.getId(), "PAST", 0L, 20L);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByOwner_whenStateFuture_thenReturnedListOfBookingDtoStateFuture() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.getBookingsByOwnerAndStatus_Future(anyLong())).thenReturn(List.of(booking));

        bookingDto.setStart(LocalDateTime.now().plusMonths(2));
        bookingDto.setEnd(LocalDateTime.now().plusMonths(4));
        bookingDto.getItem().setId(item.getId());
        bookingService.addBooking(user.getId(), bookingDto);

        List<BookingDto> bookingDtos = bookingService.getAllBookingsByOwner(user.getId(), "FUTURE", 0L, 20L);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByOwner_whenStatusWaiting_thenReturnedListOfBookingDtoStatusWaiting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.getBookingByOwnerAndStatus(anyLong(), any())).thenReturn(List.of(booking));

        bookingDto.setStart(LocalDateTime.now().plusMonths(2));
        bookingDto.setEnd(LocalDateTime.now().plusMonths(4));
        bookingDto.getItem().setId(item.getId());
        bookingService.addBooking(user.getId(), bookingDto);

        List<BookingDto> bookingDtos = bookingService.getAllBookingsByOwner(user.getId(), "WAITING", 0L, 20L);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByOwner_whenStatusRejected_thenReturnedListOfBookingDtoStatusRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingRepository.getBookingByOwnerAndStatus(anyLong(), any())).thenReturn(List.of(booking));

        bookingDto.setStart(LocalDateTime.now().plusMonths(2));
        bookingDto.setEnd(LocalDateTime.now().plusMonths(4));
        bookingDto.getItem().setId(item.getId());

        bookingService.addBooking(user.getId(), bookingDto);
        bookingService.changeStatus(2L, bookingDto.getId(), false);

        List<BookingDto> bookingDtos = bookingService.getAllBookingsByOwner(user.getId(), "REJECTED", 0L, 20L);

        assertEquals(1L, bookingDtos.get(0).getId());
        assertEquals(user, bookingDtos.get(0).getBooker());
        assertEquals(user.getId(), bookingDtos.get(0).getBookerId());
        assertEquals(BookingStatus.REJECTED, bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllBookingsByOwner_whenUnsupportedStatus_thenUnsupportedStatusException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        user.setId(2L);
        UnsupportedStatusException thrown = Assertions.assertThrows(UnsupportedStatusException.class, () -> {
            bookingService.getAllBookingsByBooker(user.getId(), "UNSUPPORTED", null, null);
        });

        assertEquals("Unknown state: UNSUPPORTED_STATUS", thrown.getMessage());
    }

    @Test
    void createPageRequest_whenFromOrSizeNull_thenReturnedPageRequestNull() {
        PageRequest pageRequest = BookingServiceImpl.createPageRequest(null, null);

        assertNull(pageRequest);
    }

    @Test
    void createPageRequest_whenFromOrSizeLessZero_thenThrownBadRequestException() {
        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            BookingServiceImpl.createPageRequest(-1L, 10L);
        });

        assertEquals("Индекс первого элемента и количество элементов не могут быть отрицательными", thrown.getMessage());
    }

    @Test
    void createPageRequest_whenFromOrSizeEqualZero_thenThrownBadRequestException() {
        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            BookingServiceImpl.createPageRequest(0L, 0L);
        });

        assertEquals("Нечего возвращать", thrown.getMessage());
    }

    @Test
    void createPageRequest_whenFromOrSizeEqualZero_thenReturnedPageRequest() {
        int pageNumber = (int) (1L / 20L);
        PageRequest pageRequestTest = PageRequest.of(pageNumber, Math.toIntExact(20L));

        PageRequest pageRequest = BookingServiceImpl.createPageRequest(1L, 20L);

        assertEquals(pageRequestTest, pageRequest);
    }
}