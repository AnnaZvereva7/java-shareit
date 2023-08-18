package ru.practicum.shareit.request.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.ItemRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ItemRequestRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ItemRequest> findByRequestorIdNot(Long userId, int from, int size) {
        return entityManager.createQuery("SELECT i FROM ItemRequest i JOIN FETCH i.requestor r where r.id <>:userId ORDER BY i  .created DESC",
                        ItemRequest.class)
                .setParameter("userId", userId)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }
}
