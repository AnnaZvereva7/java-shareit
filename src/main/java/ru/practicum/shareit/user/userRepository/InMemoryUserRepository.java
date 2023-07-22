package ru.practicum.shareit.user.userRepository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotUniqueEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserRepository implements UserRepository {
    Map<Integer, User> users = new HashMap<>();
    int lastId = 0;

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
    public User updatePartial(User user) {
        User oldUser = users.get(user.getId());
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (isEmailUnique(user)) {
                oldUser.setEmail(user.getEmail());
            } else {
                throw new NotUniqueEmailException();
            }
        }
        users.put(user.getId(), oldUser);
        return users.get(user.getId());
    }

    @Override
    public void delete(int id) {
        users.remove(id);
    }

    @Override
    public User findById(int id) {
        return users.get(id);
    }

    @Override
    public void containId(int id) {
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
