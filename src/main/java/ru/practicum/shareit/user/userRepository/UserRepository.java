package ru.practicum.shareit.user.userRepository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User save(User user);

    User updatePartial(User user);

    void delete(int id);

    User findById(int id);

    void containId(int id);

    List<User> findAll();
}
