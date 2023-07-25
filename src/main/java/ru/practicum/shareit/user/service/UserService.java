package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User save(User user);

    User updatePartial(User user);

    void delete(int id);

    User findById(int id);

    List<User> findAll();
}
