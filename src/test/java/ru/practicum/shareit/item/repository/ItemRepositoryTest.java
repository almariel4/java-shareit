package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Anna", "test@test.ru");
        item = new Item(1L, "Качели", "Качели для малышей", true, user.getId(), null);
    }

    @Test
    void search() {
        userRepository.save(user);
        itemRepository.save(item);
        PageRequest pageRequest = PageRequest.of(0, 20);
        String text = "Качели";
        List<Item> items = itemRepository.search(text,pageRequest);

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(1L, items.get(0).getId());
    }

}