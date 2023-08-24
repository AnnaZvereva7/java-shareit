package ru.practicum.shareit.item.repositiry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c JOIN FETCH c.item i JOIN FETCH c.author a " +
            "where i.id in ?1 ORDER BY c.created")
    List<Comment> findAllCommentsByItemsId(List<Long> itemsId);

}
