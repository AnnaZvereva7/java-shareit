package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotUniqueEmailException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Component
public class UserServiceImp implements UserService {
    private final UserRepository repository;

    public UserServiceImp( UserRepository userRepository) {
        this.repository = userRepository;
    }

    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public User update(long id, String name, String email) {
        User user = findById(id);
        if(name!=null&&!name.isBlank()) {
            user.setName(name);
        }
        if (email!=null&&!email.isBlank()) {
            if(repository.findUserWithSameEmail(email, id)==null) {
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
        Optional<User> userOptional=repository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else  {
            throw new NotFoundException(User.class);
        }
}

    public List<User> findAll() {
        return repository.findAll();
    }
}
