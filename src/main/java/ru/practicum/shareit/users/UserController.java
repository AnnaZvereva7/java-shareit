package ru.practicum.shareit.users;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.Marker;
import ru.practicum.shareit.users.dto.UserDto;
import ru.practicum.shareit.users.dto.UserMapper;
import ru.practicum.shareit.users.service.UserService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserMapper mapper;
    private final UserService userService;

    public UserController(UserMapper mapper, UserService userService) {
        this.mapper = mapper;
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        return mapper.toUserDto(userService.findById(userId));
    }

    @PostMapping
    public UserDto save(@RequestBody @Validated(Marker.OnCreate.class) UserDto userDto) {
        return mapper.toUserDto(userService.save(mapper.fromUserDto(userDto)));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody @Validated(Marker.OnUpdate.class) UserDto userDto) {
        return mapper.toUserDto(userService.update(userId, userDto.getName(), userDto.getEmail()));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll()
                .stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

}
