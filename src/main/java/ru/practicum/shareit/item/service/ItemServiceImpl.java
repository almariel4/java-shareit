package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
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
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item;
        if (itemDto.getName().isBlank()
                || itemDto.getDescription() == null
                || itemDto.getAvailable() == null) {
            throw new BadRequestException("Не заполнены необходимые поля новой вещи");
        }
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        item = ItemMapper.mapToItem(userId, itemDto);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto editItem(Long userId, Long itemId, ItemDto itemDto) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id = " + itemId + " не найдена"));
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getItemsByUser(Long userId, Long from, Long size) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));

        PageRequest pageRequest = BookingServiceImpl.createPageRequest(from, size);
        List<Item> items;
        if (pageRequest != null) {
            items = itemRepository.findItemsByOwnerOrderById(userId, pageRequest);
        } else {
            items = itemRepository.findItemsByOwnerOrderById(userId);
        }
        List<ItemDto> itemList = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = setBookings(userId, item);
            itemList.add(itemDto);
        }

        return itemList;
    }

    public ItemDto setBookings(Long userId, Item item) {
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        if (userId.equals(item.getOwner())) {
            Booking lastBooking = bookingRepository.getLastBooking(item.getId()).orElse(null);
            itemDto.setLastBooking(lastBooking);
            Booking nextBooking = bookingRepository.getNextBooking(item.getId()).orElse(null);
            itemDto.setNextBooking(nextBooking);
        }
        return itemDto;
    }

    @Transactional
    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id = " + itemId + " не найдена"));
        ItemDto itemDto = setBookings(userId, item);
        commentRepository.getCommentsByItemId(itemId).ifPresent(comments -> itemDto.setComments(comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList())));
        return itemDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchForItems(Long userId, String text, Long from, Long size) {
        List<ItemDto> items = new ArrayList<>();
        PageRequest pageRequest = BookingServiceImpl.createPageRequest(from, size);
        if (!text.isBlank()) {
            if (pageRequest != null) {
                items = itemRepository.search(text).stream().map(ItemMapper::mapToItemDto).collect(Collectors.toList());
            } else {
                items = itemRepository.search(text, pageRequest).stream()
                        .map(ItemMapper::mapToItemDto).collect(Collectors.toList());
            }
        }
        return items;
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText().isBlank()) {
            throw new BadRequestException("Текст комментария не может быть пустым");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id = " + itemId + " не найдена"));
        List<Booking> bookings = bookingRepository.getBookingsByBookerId_OrderByStartDesc(userId);
        Optional<Booking> wasBooked = bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .findFirst();
        if (wasBooked.isEmpty()) {
            throw new BadRequestException("Пользователь не может оставить комментарий к вещи");
        }
        Comment comment = CommentMapper.mapToComment(item, user, commentDto);
//        commentRepository.save(comment);
        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

}
