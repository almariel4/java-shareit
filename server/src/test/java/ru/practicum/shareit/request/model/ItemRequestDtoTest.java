package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        User user = new User(1L, "Anna", "test@test.ru");

        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Требуются качели для малышей", user, LocalDateTime.of(2023, 5, 23, 12, 0), new ArrayList<>());

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Требуются качели для малышей");
        assertThat(result).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.requestor.name").isEqualTo("Anna");
        assertThat(result).extractingJsonPathStringValue("$.requestor.email").isEqualTo("test@test.ru");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}