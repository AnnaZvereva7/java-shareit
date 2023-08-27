package ru.practicum.shareit.users.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotUniqueEmailException;
import ru.practicum.shareit.users.model.User;
import ru.practicum.shareit.users.UserRepository;

import java.util.List;

@Component
public class UserServiceImp implements UserService {
    private final UserRepository repository;

    public UserServiceImp(UserRepository userRepository) {
        this.repository = userRepository;
    }

    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public User update(long id, String name, String email) {
        User user = findById(id);
        if (name != null && !name.isBlank()) {
            user.setName(name);
        }
        if (email != null && !email.isBlank()) {
            if (repository.findUserWithSameEmail(email, id) == null) {
                user.setEmail(email);
            } else {
                throw new NotUniqueEmailException();
            }
        }
        return repository.saveAndFlush(user);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

    public User findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(User.class));
    }

    public List<User> findAll() {
        return repository.findAll();
    }
}
