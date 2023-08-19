package ru.practicum.shareit.users;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.users.model.User;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Slf4j
@Sql({"/schemaTest.sql", "/import_tables.sql"})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1 = new User(1L, "name", "email@mail.ru");
    private User user3 = new User(3L, "name33", "email3@mail.ru");
    private User user4 = new User(4L, "name4", "email4@mail.ru");
    private User user5 = new User(4L, "name5", "email mail.ru");

    @Test
    void findUserWithSameEmail_whenUserReturned() {
        User found = userRepository.findUserWithSameEmail("email@mail.ru", 2L);

        assertThat(found.getId()).isEqualTo(user1.getId());
        assertThat(found.getName()).isEqualTo(user1.getName());
        assertThat(found.getEmail()).isEqualTo(user1.getEmail());
    }

    @Test
    void findUserWithSameEmail_whenUserEmpty() {
        User found = userRepository.findUserWithSameEmail("email@mail.ru", 1L);

        assertThat(found).isEqualTo(null);
    }

    @Test
    void save() {
        User newUser = userRepository.save(user4);
        assertThat(newUser.getId()).isEqualTo(4L);
        user3 = userRepository.save(user3);
        assertEquals(user3.getName(), "name33");
//        Throwable thrown = catchThrowable(() -> {
//           userRepository.save(user5);
//        });
//        assertThat(thrown).isInstanceOf(ConstraintViolationException.class);

    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void deleteById() {
        userRepository.deleteById(3L);
        assertThat(userRepository.findAll().size()).isEqualTo(2);

        Throwable thrown = catchThrowable(() -> {
            userRepository.deleteById(8L);
        });
        assertThat(thrown).isInstanceOf(EmptyResultDataAccessException.class);

        assertThat(userRepository.findAll().size()).isEqualTo(2);
        userRepository.deleteAll();
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findById() {
        Optional<User> foundUser = userRepository.findById(1L);
        assertThat(foundUser).isPresent();
        assertEquals(foundUser.get().getId(), user1.getId());
        assertEquals(foundUser.get().getName(), user1.getName());
        assertEquals(foundUser.get().getEmail(), user1.getEmail());
    }

    @Test
    void findById_whenEmpty() {
        Optional<User> foundUser = userRepository.findById(99L);
        assertThat(foundUser).isEmpty();
    }


}