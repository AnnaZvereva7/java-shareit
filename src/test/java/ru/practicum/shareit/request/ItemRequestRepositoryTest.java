package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.OffsetBasedPageRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql({"/schemaTest.sql", "/import_tables.sql"})
class ItemRequestRepositoryTest {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ItemRequestRepository repository;

    @Test
    void findByRequestorId_whenEmpty() {
        List<ItemRequest> requestList = repository.findByRequestorId(1L);
        assertEquals(0, requestList.size());
    }

    @Test
    void findByRequestorId_when1Request() {
        List<ItemRequest> requestList = repository.findByRequestorId(3L);

        assertThat(requestList.size() == 1);
        assertThat(requestList.get(0).getId()).isEqualTo(3L);
        assertThat(requestList.get(0).getDescription()).isEqualTo("ItemDescription3");
        assertThat(requestList.get(0).getRequestor().getId()).isEqualTo(3L);
        assertThat(requestList.get(0).getCreated().format(FORMATTER)).isEqualTo("2023-07-01 11:30:00");
    }

    @Test
    void findByRequestorId_whenSorted() {
        List<ItemRequest> requestList = repository.findByRequestorId(2L);

        assertThat(requestList.size() == 2);
        assertThat(requestList.get(0).getId()).isEqualTo(2L);
        assertThat(requestList.get(0).getDescription()).isEqualTo("ItemDescription2");
        assertThat(requestList.get(0).getRequestor().getId()).isEqualTo(2L);
        assertThat(requestList.get(0).getCreated().format(FORMATTER)).isEqualTo("2023-08-10 11:30:00");
        assertThat(requestList.get(1).getId()).isEqualTo(1L);
        assertThat(requestList.get(1).getDescription()).isEqualTo("ItemDescription1");
        assertThat(requestList.get(1).getRequestor().getId()).isEqualTo(2L);
        assertThat(requestList.get(1).getCreated().format(FORMATTER)).isEqualTo("2023-08-01 11:30:00");
    }

    @Test
    void findAll_whenNotOwner() {
        //from 1 size 2 result request2 and3
        List<ItemRequest> requests = repository.findByRequestorIdNot(1L, new OffsetBasedPageRequest(1, 2));
        assertEquals(2, requests.size());
        assertEquals(1L, requests.get(0).getId());
        assertEquals(3L, requests.get(1).getId());
    }

    @Test
    void findAll_whenOwner() {
        List<ItemRequest> requests = repository.findByRequestorIdNot(2L, new OffsetBasedPageRequest(0, 2));
        assertEquals(1, requests.size());
        assertEquals(3L, requests.get(0).getId());
    }

    @Test
    void findById_whenWrong() {
        Optional<ItemRequest> itemRequest = repository.findById(4L);
        assertThat(itemRequest).isEmpty();
    }

    @Test
    void findById_whenOk() {
        Optional<ItemRequest> itemRequest = repository.findById(1L);
        assertThat(itemRequest).isPresent();
        assertEquals(1L, itemRequest.get().getId());
        assertEquals("ItemDescription1", itemRequest.get().getDescription());
        assertEquals("2023-08-01 11:30:00", itemRequest.get().getCreated().format(FORMATTER));
    }

}