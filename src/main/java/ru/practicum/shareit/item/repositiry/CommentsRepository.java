package ru.practicum.shareit.item.repositiry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select comments.id as id, comments.item_id as itemId, comments.text as text, users.name as authorName, comments.created as created " +
            "from comments inner join users on comments.author_id=users.id " +
            "where comments.item_id in ?1 order by comments.created", nativeQuery = true)
    List<CommentDto> findAllCommentsByItemsId(List<Long> itemsId);


}
