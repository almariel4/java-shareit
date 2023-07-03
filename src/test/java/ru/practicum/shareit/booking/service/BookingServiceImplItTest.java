package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplItTest {

    private final BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    private BookingDto bookingDto;
    private Item item;
    private ItemDto itemDto;
    private User user;
    private UserDto userDto;
    private User user_Kris;
    private UserDto userDto_Kris;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Anna", "test@test.ru");
        userDto = new UserDto(1L, "Anna", "test@test.ru");
        user_Kris = new User(2L, "Kristina", "testKristina@test.ru");
        userDto_Kris = new UserDto(2L, "Kristina", "testKristina@test.ru");
        item = new Item(1L, "Качели", "Качели для малышей", true, 2L, null);
        itemDto = new ItemDto(1L, "Качели", "Качели для малышей", true, null, null, null, new ArrayList<>());
        bookingDto = new BookingDto(1L,
                LocalDateTime.of(2023, 5, 30, 12, 0),
                LocalDateTime.of(2023, 7, 30, 12, 0),
                item.getId(), item, user_Kris, user_Kris.getId(), BookingStatus.WAITING);

        userService.createUser(userDto);
        userService.createUser(userDto_Kris);
        itemService.addItem(user.getId(), itemDto);
        bookingDto.setStart(LocalDateTime.now().plusMonths(2));
        bookingDto.setEnd(LocalDateTime.now().plusMonths(4));
    }

    @Test
    void addBooking() {
        bookingService.addBooking(user_Kris.getId(), bookingDto);

        BookingDto bookingDtoTest = bookingService.getBooking(user.getId(), bookingDto.getId());

        assertThat(bookingDtoTest.getId(), notNullValue());
        assertThat(bookingDtoTest.getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingDtoTest.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingDtoTest.getBooker().getName(), equalTo(bookingDto.getBooker().getName()));
        assertThat(bookingDtoTest.getBookerId(), equalTo(bookingDto.getBookerId()));
        assertThat(bookingDtoTest.getStatus(), equalTo(bookingDto.getStatus()));
    }

    @Test
    void changeStatus() {
        bookingService.addBooking(user_Kris.getId(), bookingDto);
        bookingService.changeStatus(user.getId(), bookingDto.getId(), true);

        BookingDto bookingDtoTest = bookingService.getBooking(user.getId(), bookingDto.getId());

        assertThat(bookingDtoTest.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getBooking() {
        bookingService.addBooking(user_Kris.getId(), bookingDto);
        BookingDto bookingDtoTest = bookingService.getBooking(user.getId(), bookingDto.getId());

        assertThat(bookingDtoTest.getId(), equalTo(bookingDto.getId()));
        assertThat(bookingDtoTest.getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingDtoTest.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingDtoTest.getBooker().getName(), equalTo(bookingDto.getBooker().getName()));
        assertThat(bookingDtoTest.getBookerId(), equalTo(bookingDto.getBookerId()));
        assertThat(bookingDtoTest.getStatus(), equalTo(bookingDto.getStatus()));
    }

    @Test
    void getAllBookingsByBooker() {
        bookingService.addBooking(user_Kris.getId(), bookingDto);

        List<BookingDto> bookingDtoTest = bookingService.getAllBookingsByBooker(user_Kris.getId(), "ALL", 0L, 20L);

        assertThat(bookingDtoTest.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(bookingDtoTest.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingDtoTest.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingDtoTest.get(0).getBooker().getName(), equalTo(bookingDto.getBooker().getName()));
        assertThat(bookingDtoTest.get(0).getBookerId(), equalTo(bookingDto.getBookerId()));
        assertThat(bookingDtoTest.get(0).getStatus(), equalTo(bookingDto.getStatus()));
    }

    @Test
    void getAllBookingsByOwner() {
        bookingService.addBooking(user_Kris.getId(), bookingDto);

        List<BookingDto> bookingDtoTest = bookingService.getAllBookingsByOwner(user.getId(), "ALL", 0L, 20l);

        assertThat(bookingDtoTest.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(bookingDtoTest.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingDtoTest.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingDtoTest.get(0).getBooker().getName(), equalTo(bookingDto.getBooker().getName()));
        assertThat(bookingDtoTest.get(0).getBookerId(), equalTo(bookingDto.getBookerId()));
        assertThat(bookingDtoTest.get(0).getStatus(), equalTo(bookingDto.getStatus()));
    }
}