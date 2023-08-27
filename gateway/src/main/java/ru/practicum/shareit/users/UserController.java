package ru.practicum.shareit.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Marker;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable Long userId) {
        log.info("Get user with userId={}", userId);
        return userClient.findById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Validated(Marker.OnCreate.class) UserDto userDto) {
        log.info("Save new user {}", userDto);
        return userClient.save(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId,
                                         @RequestBody @Validated(Marker.OnUpdate.class) UserDto userDto) {
        log.info("Update user id= {} to {}", userId, userDto);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        log.info("delete user id={}", userId);
        return userClient.delete(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Find all users");
        return userClient.findAll();
    }
}
