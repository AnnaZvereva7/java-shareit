package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.OffsetBasedPageRequest;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT i FROM ItemRequest i JOIN FETCH i.requestor r where r.id =:userId ORDER BY i.created DESC")
    List<ItemRequest> findByRequestorId(@Param("userId") Long requestorId);

    @Query("SELECT i FROM ItemRequest i JOIN FETCH i.requestor r where r.id <>:userId ORDER BY i.created DESC")
    List<ItemRequest> findByRequestorIdNot(@Param("userId") Long userId, OffsetBasedPageRequest pageRequest);


}
