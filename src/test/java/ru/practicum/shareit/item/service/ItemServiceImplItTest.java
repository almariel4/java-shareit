package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplItTest {

    private final ItemService itemService;
    @Autowired
    private UserService userService;

    private UserDto userDto;
    private ItemDto itemDto;
    private Item item;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "Anna", "test@test.ru");
        itemDto = new ItemDto(1L, "Качели", "Качели для малышей", true, null, null, null, new ArrayList<>());
        item = new Item(1L, "Качели", "Качели для малышей", true, 2L, 1L);
    }

    @Test
    void addItem_whenItemIsValidated_thenReturnedItemDto() {
        userService.createUser(userDto);
        itemService.addItem(userDto.getId(), itemDto);

        ItemDto itemDtoTest = itemService.getItem(userDto.getId(), itemDto.getId());

        assertThat(itemDtoTest.getId(), notNullValue());
        assertThat(itemDtoTest.getName(), equalTo(itemDto.getName()));
        assertThat(itemDtoTest.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemDtoTest.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(itemDtoTest.getRequestId(), equalTo(itemDto.getRequestId()));
        assertThat(itemDtoTest.getLastBooking(), equalTo(itemDto.getLastBooking()));
        assertThat(itemDtoTest.getNextBooking(), equalTo(itemDto.getNextBooking()));
    }

    @Test
    void editItem_whenItemExists_thenReturnedUpdatedItemDto() {
        userService.createUser(userDto);
        itemService.addItem(userDto.getId(), itemDto);

        itemDto.setName("Updated Item Name");
        itemDto.setDescription("Updated Item Description");
        itemDto.setAvailable(false);

        itemService.editItem(userDto.getId(), itemDto.getId(), itemDto);
        ItemDto itemDtoTest = itemService.getItem(userDto.getId(), itemDto.getId());

        assertThat(itemDtoTest.getId(), equalTo(itemDto.getId()));
        assertThat(itemDtoTest.getName(), equalTo(itemDto.getName()));
        assertThat(itemDtoTest.getName(), equalTo(itemDto.getName()));
        assertThat(itemDtoTest.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemDtoTest.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(itemDtoTest.getRequestId(), equalTo(itemDto.getRequestId()));
        assertThat(itemDtoTest.getLastBooking(), equalTo(itemDto.getLastBooking()));
        assertThat(itemDtoTest.getNextBooking(), equalTo(itemDto.getNextBooking()));
    }

    @Test
    void getItemsByUser_whenItemsExist_thenReturnedListOfItemDto() {
        userService.createUser(userDto);
        itemService.addItem(userDto.getId(), itemDto);

        List<ItemDto> itemDtoTestList = itemService.getItemsByUser(userDto.getId(), 0L, 20L);

        assertThat(itemDtoTestList.get(0).getId(), equalTo(itemDto.getId()));
        assertThat(itemDtoTestList.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(itemDtoTestList.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(itemDtoTestList.get(0).getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemDtoTestList.get(0).getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(itemDtoTestList.get(0).getRequestId(), equalTo(itemDto.getRequestId()));
        assertThat(itemDtoTestList.get(0).getLastBooking(), equalTo(itemDto.getLastBooking()));
        assertThat(itemDtoTestList.get(0).getNextBooking(), equalTo(itemDto.getNextBooking()));
    }

    @Test
    void getItem_whenItemExists_thenReturnedItemDto() {
        userService.createUser(userDto);
        itemService.addItem(1L, itemDto);
        ItemDto itemDtoTest = itemService.getItem(userDto.getId(), itemDto.getId());

        assertThat(itemDtoTest.getId(), equalTo(itemDto.getId()));
        assertThat(itemDtoTest.getName(), equalTo(itemDto.getName()));
        assertThat(itemDtoTest.getName(), equalTo(itemDto.getName()));
        assertThat(itemDtoTest.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemDtoTest.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(itemDtoTest.getRequestId(), equalTo(itemDto.getRequestId()));
        assertThat(itemDtoTest.getLastBooking(), equalTo(itemDto.getLastBooking()));
        assertThat(itemDtoTest.getNextBooking(), equalTo(itemDto.getNextBooking()));
    }

    @Test
    void searchForItems_whenSearchLineIsNotBlank_thenReturnedListOfItemDto() {
        userService.createUser(userDto);
        itemService.addItem(userDto.getId(), itemDto);

        List<ItemDto> itemDtoTestList = itemService.searchForItems(userDto.getId(), "Качели", 0L, 20L);

        assertThat(itemDtoTestList.get(0).getId(), equalTo(itemDto.getId()));
        assertThat(itemDtoTestList.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(itemDtoTestList.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(itemDtoTestList.get(0).getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemDtoTestList.get(0).getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(itemDtoTestList.get(0).getRequestId(), equalTo(itemDto.getRequestId()));
        assertThat(itemDtoTestList.get(0).getLastBooking(), equalTo(itemDto.getLastBooking()));
        assertThat(itemDtoTestList.get(0).getNextBooking(), equalTo(itemDto.getNextBooking()));
    }
}