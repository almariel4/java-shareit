package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingDto addBooking(Long userId, BookingDto bookingDto) {
        Optional<Item> itemOptional = Optional.ofNullable(itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Вещь с id = " + bookingDto.getItemId() + " не найдена")));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Item item = itemOptional.get();
        if (userId.equals(item.getOwner())) {
            throw new NotFoundException("Владелец вещи не может создать на нее бронирование");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь недоступна для бронирования");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new BadRequestException("Должны быть заполнены дата начала и дата окончания бронирования");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата начала бронирования не должна быть раньше текущей даты и времени");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new BadRequestException("Дата окончания бронирования не должна быть позже даты начала");
        }
        Booking booking = BookingMapper.mapToBooking(bookingDto, item, user);
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto changeStatus(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));
        if (!booking.getItem().getOwner().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи для бронирования");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BadRequestException("Владелец вещи уже одобрил бронь вещи");
        }
        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(status);
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));
        BookingDto bookingDto;
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().equals(userId)) {
            bookingDto = BookingMapper.mapToBookingDto(booking);
        } else {
            throw new NotFoundException("Бронирование с id = " + bookingId + "у пользователя с id "
                    + "не найдено");
        }
        return bookingDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingsByBooker(Long userId, String state, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        if (state != null && state.equals("UNSUPPORTED")) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        BookingState bookingState = state == null ? BookingState.ALL : BookingState.valueOf(state);
        List<Booking> bookings;

        switch (bookingState) {
            case ALL:
                if (pageable != null) {
                    bookings = bookingRepository.getBookingsByBookerId_OrderByStartDesc(userId, pageable);
                } else {
                    bookings = bookingRepository.getBookingsByBookerId_OrderByStartDesc(userId);
                }
                break;
            case CURRENT:
                bookings = bookingRepository.getBookingsByBookerId_OrderByStart_Current(userId);
                break;
            case PAST:
                bookings = bookingRepository.getBookingsByBookerId_OrderByStart_Past(userId);
                break;
            case FUTURE:
                bookings = bookingRepository.getBookingsByBookerId_OrderByStart_Future(userId);
                break;
            case WAITING:
            case REJECTED:
                BookingStatus status = BookingStatus.valueOf(state);
                bookings = bookingRepository.getBookingsByBookerId_OrderByBookerId(userId, status);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::mapToBookingDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingsByOwner(Long userId, String state, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        if (state != null && state.equals("UNSUPPORTED")) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        BookingState bookingState = state == null ? BookingState.ALL : BookingState.valueOf(state);

        List<Booking> bookings;

        switch (bookingState) {
            case ALL:
                if (pageable != null) {
                    bookings = bookingRepository.getBookingsByItemOwnerOrderByStartDesc(userId, pageable);
                } else {
                    bookings = bookingRepository.getBookingsByItemOwnerOrderByStartDesc(userId);
                }
                break;
            case CURRENT:
                bookings = bookingRepository.getBookingsByOwnerAndStatus_Current(userId);
                break;
            case PAST:
                bookings = bookingRepository.getBookingsByOwnerAndStatus_Past(userId);
                break;
            case FUTURE:
                bookings = bookingRepository.getBookingsByOwnerAndStatus_Future(userId);
                break;
            case WAITING:
            case REJECTED:
                BookingStatus status = BookingStatus.valueOf(state);
                bookings = bookingRepository.getBookingByOwnerAndStatus(userId, status);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::mapToBookingDto).collect(Collectors.toList());
    }

}
