package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private final String text = "Качели";

    @BeforeEach
    void setUp() {
        item = new Item(1L, "Качели", "Качели для малышей", true, 2L, 1L);
    }

    @Test
    void search() {
        itemRepository.save(item);

        List<Item> items = itemRepository.search(text);

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(1L, items.get(0).getId());
    }

    @Test
    void searchPagebale() {
        itemRepository.save(item);

        List<Item> items = itemRepository.search(text);

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(1L, items.get(0).getId());
    }

}