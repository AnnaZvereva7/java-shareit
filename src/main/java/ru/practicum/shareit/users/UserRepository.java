package ru.practicum.shareit.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.users.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.email like ?1 and u.id <>?2")
    User findUserWithSameEmail(String email, long id);

}
