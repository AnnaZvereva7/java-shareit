package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

@Repository
public interface DBUserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query("update User u set u.name=:name where u.id=:id")
    void updateName(@Param("id") Long id, @Param("name") String name);


    @Modifying
    @Query("update User u set u.email=:email where u.id=:id")
    void updateEmail(@Param("id") Long id, @Param("email") String email);

    @Query("select u from User u where u.email like ?1 and u.id <>?2")
    User isEmailUnique(String email, long id);
}
