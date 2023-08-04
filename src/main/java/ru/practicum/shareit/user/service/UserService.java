package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User save(User user);

    User update(long id, String name, String email);

    void delete(long id);

    User findById(long id);

    List<User> findAll();
}
