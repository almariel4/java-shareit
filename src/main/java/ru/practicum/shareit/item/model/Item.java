package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private Long owner;
    private ItemRequest request;
}
