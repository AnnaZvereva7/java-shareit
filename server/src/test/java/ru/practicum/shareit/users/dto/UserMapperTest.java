package ru.practicum.shareit.users.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.users.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    UserMapper mapper = new UserMapper();

    @Test
    void toUserDto() {
        User user = new User(1L, "name", "email@mail.ru");
        UserDto userDto = mapper.toUserDto(user);
        assertEquals(1L, userDto.getId());
        assertEquals("name", userDto.getName());
        assertEquals("email@mail.ru", userDto.getEmail());
    }

    @Test
    void fromUserDto() {
        UserDto userDto = new UserDto(1L, "name", "email@mail.ru");
        User user = mapper.fromUserDto(userDto);
        assertEquals(1L, user.getId());
        assertEquals("name", user.getName());
        assertEquals("email@mail.ru", user.getEmail());
    }
}