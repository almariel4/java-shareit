package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Отличные качели", 1L, "Anna",
                LocalDateTime.of(2023, 5, 30, 12, 0));

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличные качели");
        assertThat(result).extractingJsonPathNumberValue("$.item").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Anna");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}