package ru.practicum.shareit.item.repositiry;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.OffsetBasedPageRequest;

import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository repository;

    private Item item1 = new Item(1L, "name", "Description", true, 1L, 1L);
    private Item item2 = new Item(2L, "name2", "description2", false, 1L, 1L);
    private Item item3 = new Item(3L, "name3", "dEscription3", true, 1L, 2L);


    @Test
    @Sql({"/schemaTest.sql"})
    void findByOwnerId_whenEmpty() {
        List<Item> items = repository.findAllByOwnerId(1, new OffsetBasedPageRequest(0, 20));
        assertThat(items).isEqualTo(List.of());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByOwnerId_whenAll() {
        List<Item> items = repository.findAllByOwnerId(1L, new OffsetBasedPageRequest(0, 20));
        assertThat(items.size()).isEqualTo(3);
        assertThat(items.get(0)).isEqualTo(item1);
        assertThat(items.get(1)).isEqualTo(item2);
        assertThat(items.get(2)).isEqualTo(item3);
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByOwnerId_whenFrom1Size2() {
        List<Item> items = repository.findAllByOwnerId(1, new OffsetBasedPageRequest(1, 1));
        assertEquals(items.size(), 1);
        assertEquals(items.get(0), item2);
    }


    @Test
    @Sql({"/schemaTest.sql"})
    void findByText_whenEmpty() {
        List<Item> items = repository.findByTextAndAvailableTrue("description",
                new OffsetBasedPageRequest(0, 20));
        assertThat(items).isEqualTo(List.of());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByText_whenAll() {
        List<Item> items = repository.findByTextAndAvailableTrue("description",
                new OffsetBasedPageRequest(0, 20));
        assertEquals(items.size(), 2);
        assertEquals(items.get(0), item1);
        assertEquals(items.get(1), item3);
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByText_whenFrom1Size2() {
        List<Item> items = repository.findByTextAndAvailableTrue("description", new OffsetBasedPageRequest(1, 2));
        assertThat(items.size()).isEqualTo(1);
        assertEquals(items.get(0), item3);
    }

    @Test
    @Sql({"/schemaTest.sql"})
    void findByRequestIdIn_whenEmpty() {
        List<Item> items = repository.findByRequestIdIn(List.of(1L));
        assertEquals(0, items.size());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByRequestIdIn_whenListOf1() {
        List<Item> items = repository.findByRequestIdIn(List.of(1L));
        assertEquals(2, items.size());
        assertEquals(item1, items.get(0));
        assertEquals(item2, items.get(1));
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByRequestIdIn_whenListOf2() {
        List<Item> items = repository.findByRequestIdIn(List.of(1L, 2L));
        assertEquals(3, items.size());
        assertEquals(item1, items.get(0));
        assertEquals(item2, items.get(1));
        assertEquals(item3, items.get(2));
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void save() {
        assertEquals(6, repository.findAll().size());
        Item item7 = new Item(7L, "name7", "description7", true, 1L, 2L);
        repository.save(item7);
        assertEquals(7, repository.findAll().size());
        Optional<Item> found = repository.findById(7L);
        assertThat(found).isPresent();
        assertEquals(7, found.get().getId());
        assertEquals("name7", found.get().getName());
        assertEquals("description7", found.get().getDescription());
        assertEquals(TRUE, found.get().getAvailable());
        assertEquals(1, found.get().getOwnerId());
        assertEquals(2, found.get().getRequestId());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void delete() {
        repository.deleteById(3L);
        assertEquals(5, repository.findAll().size());

        Throwable thrown = catchThrowable(() -> {
            repository.deleteById(8L);
        });
        assertThat(thrown).isInstanceOf(EmptyResultDataAccessException.class);

        repository.deleteAll();
        assertEquals(0, repository.findAll().size());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findById() {
        Optional<Item> item = repository.findById(1L);
        assertThat(item).isPresent();
        assertEquals(1, item.get().getId());
        assertEquals("name", item.get().getName());
        assertEquals("Description", item.get().getDescription());
        assertEquals(TRUE, item.get().getAvailable());
        assertEquals(1, item.get().getOwnerId());
        assertEquals(1, item.get().getRequestId());

        Optional<Item> item10 = repository.findById(10L);
        assertThat(item10).isEmpty();
    }

}