package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Integer request;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentDto> comments;
}
