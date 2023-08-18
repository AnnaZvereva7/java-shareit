package ru.practicum.shareit.item.repositiry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.OffsetBasedPageRequest;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByRequestIdIn(List<Long> requestsId);

    @Query("SELECT i FROM Item i where (lower(i.name) like concat('%', :text,'%') " +
            "or lower(i.description) like concat('%', :text,'%')) " +
            "and i.available=TRUE ORDER BY i.id")
    List<Item> findByTextAndAvailableTrue(String text, OffsetBasedPageRequest pageRequest);


    @Query("SELECT i FROM Item i where i.ownerId=:userId ORDER BY i.id")
   List<Item> findAllByOwnerId(long userId, OffsetBasedPageRequest request);
}

