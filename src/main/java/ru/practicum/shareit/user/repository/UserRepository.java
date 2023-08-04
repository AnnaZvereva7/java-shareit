package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User save(User user);

    //    User updatePartial(User user);
    User update(long id, String name, String email);


    void delete(long id);

    User findById(long id);

    void containId(long id);

    List<User> findAll();
}
