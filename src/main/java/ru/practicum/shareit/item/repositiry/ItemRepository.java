package ru.practicum.shareit.item.repositiry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select it from Item it where it.ownerId=:id order by id")
    List<Item> findByOwnerId(@Param("id") long userId);

    List<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(String text, String text2);

}
