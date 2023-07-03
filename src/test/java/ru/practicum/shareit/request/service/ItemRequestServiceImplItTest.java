package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceImplItTest {

    private final ItemRequestService itemRequestService;
    @Autowired
    private UserService userService;
    private User user;
    private User user_Kris;
    private UserDto userDto_Kris;
    private UserDto userDto;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Anna", "test@test.ru");
        user_Kris = new User(2L, "Kristina", "testKristina@test.ru");
        userDto_Kris = new UserDto(2L, "Kristina", "testKristina@test.ru");;
        userDto = new UserDto(1L, "Anna", "test@test.ru");
        itemRequestDto = new ItemRequestDto(1L, "Требуются качели для малышей", user_Kris, LocalDateTime.of(2023, 5, 23, 12, 0), new ArrayList<>());
    }

    @Test
    void addItemRequest() {
        userService.createUser(userDto);
        itemRequestService.addItemRequest(user.getId(), itemRequestDto);

        ItemRequestDto itemRequestDtoTest = itemRequestService.getItemRequest(user.getId(), 1L);
        assertThat(itemRequestDtoTest.getId(), notNullValue());
        assertThat(itemRequestDtoTest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestDtoTest.getRequestor(), equalTo(itemRequestDto.getRequestor()));
    }

    @Test
    void getOwnItemRequests() {
        userService.createUser(userDto);
        itemRequestService.addItemRequest(user.getId(), itemRequestDto);
        List<ItemRequestDto> itemRequestDtoTestList = itemRequestService.getOwnItemRequests(user.getId());

        assertThat(itemRequestDtoTestList.get(0).getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequestDtoTestList.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestDtoTestList.get(0).getRequestor(), equalTo(itemRequestDto.getRequestor()));
        assertThat(itemRequestDtoTestList.get(0).getItems(), equalTo(itemRequestDto.getItems()));
    }

    @Test
    void getAllWithPagination() {
        userService.createUser(userDto);
        userService.createUser(userDto_Kris);
        itemRequestService.addItemRequest(user_Kris.getId(), itemRequestDto);
        List<ItemRequestDto> itemRequestDtoTestList = itemRequestService.getAllWithPagination(user.getId(), 0L, 20L);

        hasSize(1);
        assertThat(itemRequestDtoTestList.get(0).getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequestDtoTestList.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestDtoTestList.get(0).getRequestor(), equalTo(itemRequestDto.getRequestor()));
        assertThat(itemRequestDtoTestList.get(0).getItems(), equalTo(itemRequestDto.getItems()));
    }

    @Test
    void getItemRequest() {
        userService.createUser(userDto);
        itemRequestService.addItemRequest(user.getId(), itemRequestDto);
        ItemRequestDto itemRequestDtoTest = itemRequestService.getItemRequest(user.getId(), itemRequestDto.getId());

        assertThat(itemRequestDtoTest.getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequestDtoTest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestDtoTest.getRequestor(), equalTo(itemRequestDto.getRequestor()));
    }
}