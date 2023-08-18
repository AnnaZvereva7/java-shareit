package ru.practicum.shareit.request;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepositoryImpl;
import ru.practicum.shareit.users.UserRepository;
import ru.practicum.shareit.users.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository repository;
    @Autowired
    private ItemRequestRepositoryImpl repositoryImpl;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager em;

    private User user1 = new User(null, "name", "email@mail.ru");
    private User user2 = new User(null, "name2", "email2@mail.ru");
    private ItemRequest request1 = new ItemRequest(null, "description", user1, LocalDateTime.of(2023, 8, 2, 11, 30));
    private ItemRequest request2 = new ItemRequest(null, "description2", user1, LocalDateTime.of(2023, 8, 1, 11, 30));
    private ItemRequest request3 = new ItemRequest(null, "description2", user1, LocalDateTime.of(2023, 7, 21, 11, 30));

    @BeforeEach
    void usersSepUp() {
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        userRepository.flush();
        request1.setRequestor(user1);
        request2.setRequestor(user1);
        request3.setRequestor(user1);
    }

    @AfterEach
    void deleteRequests() {
        repository.deleteAll();
        userRepository.deleteAll();

    }


    @Test
    void findByRequestorId_whenEmpty() {

        List<ItemRequest> requestList = repository.findByRequestorId(user1.getId(),
                Sort.by(Sort.Direction.DESC, "created"));

        assertThat(requestList == null);
    }

    @Test
    void findByRequestorId_when1Request() {
        request1 = repository.save(request1);
        List<ItemRequest> requestList = repository.findByRequestorId(user1.getId(),
                Sort.by(Sort.Direction.DESC, "created"));

        assertThat(requestList.size() == 1);
        assertThat(requestList.get(0).getId()).isEqualTo(request1.getId());
        assertThat(requestList.get(0).getDescription()).isEqualTo(request1.getDescription());
        assertThat(requestList.get(0).getRequestor()).isEqualTo(user1);
        assertThat(requestList.get(0).getCreated()).isEqualTo(request1.getCreated());
    }

    @Test
    void findByRequestorId_whenSorted() {
        request1 = repository.save(request1);
        request2 = repository.save(request2);
        List<ItemRequest> requestList = repository.findByRequestorId(user1.getId(),
                Sort.by(Sort.Direction.DESC, "created"));

        assertThat(requestList.size() == 2);
        assertThat(requestList.get(0).getId()).isEqualTo(request1.getId());
        assertThat(requestList.get(0).getDescription()).isEqualTo(request1.getDescription());
        assertThat(requestList.get(0).getRequestor()).isEqualTo(user1);
        assertThat(requestList.get(0).getCreated()).isEqualTo(request1.getCreated());
        assertThat(requestList.get(1).getId()).isEqualTo(request2.getId());
        assertThat(requestList.get(1).getDescription()).isEqualTo(request2.getDescription());
        assertThat(requestList.get(1).getRequestor()).isEqualTo(user1);
        assertThat(requestList.get(1).getCreated()).isEqualTo(request2.getCreated());
    }

    @Test
    void findAll_whenNotOwner() {
        request1 = repository.save(request1);
        request2 = repository.save(request2);
        request3 = repository.save(request3);
        //from 1 size 2 result request2 and3
        List<ItemRequest> requests = repositoryImpl.findByRequestorIdNot(user2.getId(), 1,2);
        assertEquals(2, requests.size());
        assertEquals(request2, requests.get(0));
        assertEquals(request3, requests.get(1));
    }
    @Test
    void findAll_whenOwner() {
        request1 = repository.save(request1);
        request2 = repository.save(request2);

        List<ItemRequest> requests = repositoryImpl.findByRequestorIdNot(1L, 0,2);
        assertEquals(0, requests.size());
    }

}