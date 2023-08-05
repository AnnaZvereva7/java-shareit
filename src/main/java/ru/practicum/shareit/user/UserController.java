package ru.practicum.shareit.user;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.Marker;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
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
    public UserDto findById(@PathVariable int userId) {
        return mapper.toUserDto(userService.findById(userId));
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public UserDto save(@RequestBody @Valid UserDto userDto) {
        return mapper.toUserDto(userService.save(mapper.fromUserDto(userDto)));
    }

    @PatchMapping("/{userId}")
    @Validated(Marker.OnUpdate.class)
    public UserDto update(@PathVariable int userId, @RequestBody @Valid UserDto userDto) {
        return mapper.toUserDto(userService.update(userId, userDto.getName(), userDto.getEmail()));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable int userId) {
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
