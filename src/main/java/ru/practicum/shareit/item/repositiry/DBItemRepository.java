package ru.practicum.shareit.item.repositiry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DBItemRepository extends JpaRepository<Item, Long> {

    @Modifying
    @Transactional
    @Query("update Item it set it.name=:name where it.id=:id")
    void updateName(@Param("id") long id, @Param("name") String name);

    @Modifying
    @Transactional
    @Query("update Item it set it.description=:description where it.id=:id")
    void updateDescription(@Param("id") long id, @Param("description") String description);

    @Modifying
    @Transactional
    @Query("update Item it set it.available=:available where it.id=:id")
    void updateAvailable(@Param("id") long id, @Param("available") boolean available);

    @Query("select it from Item it where it.ownerId=:id order by id")
    List<Item> findByOwnerId(@Param("id") long userId);


    List<Item> findByNameOrDescriptionContainingIgnoreCase(String text, String text2);
}
