package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {
        User user = new User(1L, "Anna", "test@test.ru");
        ItemDto itemDto = new ItemDto(1L, "Качели", "Качели для малышей", true, 1L, null, null, new ArrayList<>());
        Item item = ItemMapper.mapToItem(user.getId(), itemDto);
        CommentDto commentDto = new CommentDto(1L, "Отличные качели", itemDto.getId(), user.getName(),
                LocalDateTime.of(2023, 5, 30, 12, 0));

        itemDto.setComments(List.of(commentDto));

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Качели");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Качели для малышей");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo(commentDto.getText());
    }
}