package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Component
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    public UserServiceImp(@Qualifier("DBUsers") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(long id, String name, String email) {
        userRepository.update(id, name, email);
        return userRepository.findById(id);
    }

    public void delete(long id) {
        userRepository.delete(id);
    }

    public User findById(long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
