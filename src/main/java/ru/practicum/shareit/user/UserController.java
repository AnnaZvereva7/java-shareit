package ru.practicum.shareit.user;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.Marker;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable int userId) {
        return userService.findById(userId);
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public User save(@RequestBody @Valid User user) {
        return userService.save(user);
    }

    @PatchMapping("/{userId}")
    @Validated({Marker.OnUpdate.class})
    public User updatePartial(@PathVariable int userId, @RequestBody @Valid User user) {
        return userService.updatePartial(user.withId(userId));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable int userId) {
        userService.delete(userId);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

}
