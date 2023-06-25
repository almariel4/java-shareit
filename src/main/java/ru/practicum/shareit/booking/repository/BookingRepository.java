package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> getBookingsByBookerId_OrderByStartDesc(Long userId);
    List<Booking> getBookingsByBookerId_OrderByStartDesc(Long userId, PageRequest pageRequest);

    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and current_timestamp between b.start and b.end " +
            "order by b.start ")
    List<Booking> getBookingsByBookerId_OrderByStart_Current(Long userId);

    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and b.end < current_timestamp " +
            "order by b.start desc")
    List<Booking> getBookingsByBookerId_OrderByStart_Past(Long userId);

    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and b.start > current_timestamp " +
            "order by b.start desc")
    List<Booking> getBookingsByBookerId_OrderByStart_Future(Long userId);

    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> getBookingsByBookerId_OrderByBookerId(Long userId, BookingStatus status);

    List<Booking> getBookingsByItemOwnerOrderByStartDesc(Long userId);

    List<Booking> getBookingsByItemOwnerOrderByStartDesc(Long userId, PageRequest pageRequest);


    @Query("select b from Booking as b " +
            "where b.item.owner = ?1 " +
            "and current_timestamp between b.start and b.end " +
            "order by b.start desc")
    List<Booking> getBookingsByOwnerAndStatus_Current(Long userId);

    @Query("select b from Booking as b " +
            "where b.item.owner = ?1 " +
            "and b.end < current_timestamp " +
            "order by b.start desc")
    List<Booking> getBookingsByOwnerAndStatus_Past(Long userId);

    @Query("select b from Booking as b " +
            "where b.item.owner = ?1 " +
            "and b.start > current_timestamp " +
            "order by b.start desc")
    List<Booking> getBookingsByOwnerAndStatus_Future(Long userId);

    @Query("select b from Booking as b " +
            "where b.item.owner = ?1 " +
            "and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> getBookingByOwnerAndStatus(Long userId, BookingStatus status);

    @Query(nativeQuery = true, value = "select * from bookings as b " +
            "where b.item_id = ?1 " +
            "and b.status = 'APPROVED' " +
            "and b.start_date < current_timestamp " +
            "order by b.start_date desc " +
            "limit 1")
    Optional<Booking> getLastBooking(Long itemId);

    @Query(nativeQuery = true, value = "select * from bookings as b " +
            "where b.item_id = ?1 " +
            "and b.status = 'APPROVED' " +
            "and b.start_date > current_timestamp " +
            "order by b.start_date " +
            "limit 1")
    Optional<Booking> getNextBooking(Long itemId);

}
