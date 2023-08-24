package ru.practicum.shareit.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotUniqueEmailException;
import ru.practicum.shareit.users.UserRepository;
import ru.practicum.shareit.users.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

    @Mock
    private UserRepository repository;

    private UserServiceImp service;

    @BeforeEach
    public void before() {
        service = new UserServiceImp(repository);
    }

    @Test
    void save() {
        User expectedUser = new User(1L, "name", "email@mail.ru");
        when(repository.save(new User(null, "name", "email@mail.ru")))
                .thenReturn(expectedUser);
        User actualUser = service.save(new User(null, "name", "email@mail.ru"));

        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
    }

    @Test
    void update_whenStatusIsOk() {
        User user = new User(1L, "name", "email@mail.ru");
        User expectedUser = new User(1L, "nameNew", "emailNew@mail.ru");
        String name = "nameNew";
        String email = "emailNew@mail.ru";
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.findUserWithSameEmail("emailNew@mail.ru", 1L)).thenReturn(null);
        when(repository.saveAndFlush(user)).thenReturn(expectedUser);

        User actualUser = service.update(1L, name, email);

        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
    }

    @Test
    void update_whenIdIsWrong() {
        String name = "nameNew";
        String email = "emailNew@mail.ru";
        when(repository.findById(10L)).thenThrow(new NotFoundException(User.class));

        Throwable thrown = catchThrowable(() -> {
            service.update(10L, name, email);
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void update_whenEmailNotUnique() {
        User user = new User(1L, "name", "email@mail.ru");
        String name = "nameNew";
        String email = "emailExist@mail.ru";
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.findUserWithSameEmail("emailExist@mail.ru", 1L)).thenThrow(new NotUniqueEmailException());

        Throwable thrown = catchThrowable(() -> {
            service.update(1L, name, email);
        });
        assertThat(thrown).isInstanceOf(NotUniqueEmailException.class);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void delete() {
        doNothing().when(repository).deleteById(anyLong());
        service.delete(1L);
        verify(repository, times(1)).deleteById(anyLong());
    }

    @Test
    void findById() {
        User expectedUser = new User(1L, "name", "email@mail.ru");
        when(repository.findById(1L)).thenReturn(Optional.of(expectedUser));
        when(repository.findById(99L)).thenReturn(Optional.empty());
        User actualUser1 = service.findById(1L);

        assertEquals(1L, actualUser1.getId());
        assertEquals("name", actualUser1.getName());
        assertEquals("email@mail.ru", actualUser1.getEmail());

        Throwable thrown = catchThrowable(() -> {
            service.findById(99L);
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAll() {
        User user = new User(1L, "name", "email@mail.ru");
        List<User> users = List.of(user);
        when(repository.findAll()).thenReturn(users);
        List<User> actualUsers = service.findAll();

        assertEquals(1, actualUsers.size());
    }

    @Test
    void findAll_whenEmpty() {
        List<User> users = List.of();
        when(repository.findAll()).thenReturn(users);
        List<User> actualUsers = service.findAll();

        assertEquals(0, actualUsers.size());
    }
}