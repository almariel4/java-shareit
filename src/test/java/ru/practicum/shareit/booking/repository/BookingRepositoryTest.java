package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private Booking booking;
    private Booking bookingLater;
    private Item item;
    private User user;
    private User user_Kris;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Anna", "test@test.ru");
        user_Kris = new User(2L, "Kristina", "testKristina@test.ru");
        item = new Item(1L, "Качели", "Качели для малышей", true, 2L, null);
        booking = new Booking(1L,
                LocalDateTime.of(2023, 6, 30, 12, 0),
                LocalDateTime.of(2023, 7, 30, 12, 0),
                item, user, user.getId(), BookingStatus.WAITING);
        bookingLater = new Booking(1L,
                LocalDateTime.of(2023, 7, 30, 12, 0),
                LocalDateTime.of(2023, 8, 30, 12, 0),
                item, user, user.getId(), BookingStatus.WAITING);
        userRepository.save(user);
        userRepository.save(user_Kris);
        itemRepository.save(item);
    }

/*    @Test
    void getBookingsByBookerId_OrderByStart_Current() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking);
        bookingRepository.save(bookingLater);
        List<Booking> bookings = bookingRepository.getBookingsByBookerId_OrderByStart_Current(user.getId());

        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }*/

/*    @Test
    void getBookingsByBookerId_OrderByStart_Past() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking);
        bookingRepository.save(bookingLater);

        List<Booking> bookings = bookingRepository.getBookingsByBookerId_OrderByStart_Past(user.getId());

        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }*/

    @Test
    void getBookingsByBookerId_OrderByStart_Future() {
        bookingRepository.save(booking);
        bookingRepository.save(bookingLater);

        List<Booking> bookings = bookingRepository.getBookingsByBookerId_OrderByStart_Future(user.getId());

        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void getBookingsByBookerId_OrderByBookerId() {
        bookingRepository.save(booking);
        bookingRepository.save(bookingLater);

        List<Booking> bookings = bookingRepository.getBookingsByBookerId_OrderByBookerId(user.getId(), BookingStatus.WAITING);

        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

/*    @Test
    void getBookingsByOwnerAndStatus_Current() {
        booking.setStart(LocalDateTime.now().minusMonths(4));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.getItem().setId(item.getId());
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.getBookingsByOwnerAndStatus_Current(user.getId());

        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }*/

/*    @Test
    void getBookingsByOwnerAndStatus_Past() {
        item = new Item(1L, "Качели", "Качели для малышей", true, 2L, 1L);

        booking.setStart(LocalDateTime.now().minusMonths(4));
        booking.setEnd(LocalDateTime.now().minusMonths(2));
        booking.getItem().setId(item.getId());
        bookingRepository.save(booking);
        bookingRepository.save(bookingLater);

        List<Booking> bookings = bookingRepository.getBookingsByOwnerAndStatus_Past(user.getId());

        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }*/

    @Test
    void getBookingsByOwnerAndStatus_Future() {
        bookingRepository.save(booking);
        bookingRepository.save(bookingLater);

        List<Booking> bookings = bookingRepository.getBookingsByOwnerAndStatus_Future(user_Kris.getId());

        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void getBookingByOwnerAndStatus() {
        booking.getItem().setOwner(2L);
        bookingRepository.save(booking);
        bookingRepository.save(bookingLater);

        List<Booking> bookings = bookingRepository.getBookingByOwnerAndStatus(user_Kris.getId(), BookingStatus.WAITING);

        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void getLastBooking() {
        booking.setStart(LocalDateTime.now().minusDays(4));
        booking.setEnd(LocalDateTime.now().minusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        bookingRepository.save(bookingLater);

        Optional <Booking> booking = bookingRepository.getLastBooking(item.getId());

        assertNotNull(booking);
    }

    @Test
    void getNextBooking() {
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(4));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        bookingRepository.save(bookingLater);

        Optional <Booking> booking = bookingRepository.getNextBooking(item.getId());

        assertNotNull(booking);
    }
}