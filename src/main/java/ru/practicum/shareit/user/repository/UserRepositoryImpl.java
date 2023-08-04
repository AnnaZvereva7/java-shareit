package ru.practicum.shareit.user.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotUniqueEmailException;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("DBUsers")
public class UserRepositoryImpl implements UserRepository {
    private final DBUserRepository repository;

    public UserRepositoryImpl(DBUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        return repository.save(user);
//        if (isEmailUnique(user.getEmail(), user.getId())) {
//            return repository.save(user);
//        } else {
//            throw new NotUniqueEmailException();
//        }
    }

    @Override
    @Transactional
    public User update(long id, String name, String email) {
        if (name != null) {
            repository.updateName(id, name);
            repository.flush();
        }
        if (email != null) {
            if (isEmailUnique(email, id)) {
                repository.updateEmail(id, email);
                repository.flush();
            } else {
                throw new NotUniqueEmailException();
            }
        }
        return findById(id);
    }


    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public User findById(long id) {
        Optional<User> userOptional = repository.findById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("пользователь не найден");
        } else {
            return userOptional.get();
        }
    }

    @Override
    public void containId(long id) {
        findById(id);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    private boolean isEmailUnique(String email, long id) {
        User user = repository.isEmailUnique(email, id);
        if (user == null) {
            return true;
        } else {
            return false;
        }
    }
}
