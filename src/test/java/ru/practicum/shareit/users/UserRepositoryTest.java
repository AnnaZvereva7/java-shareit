package ru.practicum.shareit.users;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.users.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1 = new User(null, "name", "email@mail.ru");
    private User user2 = new User(null, "name2", "email2@mail.ru");

    @BeforeEach
    void setUp() {

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.flush();
    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
    }

    @Test
    void findUserWithSameEmail_whenUserReturned() {
        // when
        User found = userRepository.findUserWithSameEmail("email@mail.ru", 2L);

        // then
        assertThat(found.getId()).isEqualTo(user1.getId());
        assertThat(found.getName()).isEqualTo(user1.getName());
        assertThat(found.getEmail()).isEqualTo(user1.getEmail());
    }

    @Test
    void findUserWithSameEmail_whenUserEmpty() {
        // when
        User found = userRepository.findUserWithSameEmail("email@mail.ru", 1L);

        // then
        assertThat(found).isEqualTo(null);
    }
}