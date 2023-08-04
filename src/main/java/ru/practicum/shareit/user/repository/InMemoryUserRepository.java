package ru.practicum.shareit.user.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotUniqueEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Qualifier("ImMemoryUsers")
public class InMemoryUserRepository implements UserRepository {
    Map<Long, User> users = new HashMap<>();
    long lastId = 0;

    @Override
    public User save(User user) {
        if (isEmailUnique(user)) {
            lastId += 1;
            user.setId(lastId);
            users.put(lastId, user);
            return users.get(lastId);
        } else {
            throw new NotUniqueEmailException();
        }
    }

    @Override
    public User update(long id, String name, String email) {
        if (name != null) {
            users.get(id).setName(name);
        }
        if (email != null) {
            users.get(id).setEmail(email);
        }
        return users.get(id);
    }


    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public User findById(long id) {
        return users.get(id);
    }

    @Override
    public void containId(long id) {
        if (!users.containsKey(id)) throw new NotFoundException("Пользователя с id " + id + " не существует");
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<User>(users.values());
    }


    private boolean isEmailUnique(User user) {
        for (User thisUser : users.values()) {
            if (thisUser.getEmail().equals(user.getEmail()) && thisUser.getId() != user.getId()) {
                return false;
            }
        }
        return true;
    }
}
