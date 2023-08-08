package ru.practicum.shareit.users.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.users.model.User;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User fromUserDto(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}
