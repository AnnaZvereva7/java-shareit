package ru.practicum.shareit.item.repositiry;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.users.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentsRepositoryTest {
    @Autowired
    private CommentsRepository repository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findAllCommentsByItemsId() {
        LocalDateTime createdTime = LocalDateTime.of(2023, 8, 10, 12, 0);
        List<CommentDto> comments = repository.findAllCommentsByItemsId(List.of(1L));
        assertEquals(2, comments.size());
        assertEquals(1L, comments.get(0).getId());
        assertEquals(1L, comments.get(0).getItemId());
        assertEquals("name2", comments.get(0).getAuthorName());
        assertEquals("text1", comments.get(0).getText());
        assertEquals(createdTime.minusDays(2), comments.get(0).getCreated());

        assertEquals(3L, comments.get(1).getId());
        assertEquals(1L, comments.get(1).getItemId());
        assertEquals("name2", comments.get(1).getAuthorName());
        assertEquals("text3", comments.get(1).getText());
        assertEquals(createdTime.minusDays(1), comments.get(1).getCreated());

        comments = repository.findAllCommentsByItemsId(List.of(1L, 2L));
        assertEquals(3, comments.size());
        assertEquals(2L, comments.get(0).getId());
        assertEquals(1L, comments.get(1).getId());
        assertEquals(3L, comments.get(2).getId());
    }

    @Test
    @Sql({"/schemaTest.sql"})
    void findAllCommentsByItemsId_wHenEmpty() {
        List<CommentDto> comments = repository.findAllCommentsByItemsId(List.of(1L));
        assertEquals(0, comments.size());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void save() {
        LocalDateTime createdTime = LocalDateTime.of(2023, 8, 10, 12, 0);
        Item item = new Item(1L, "name", "Description", true, null);
        User user = new User(3L, "name3", "email3@mail.ru");
        Comment comment = new Comment(null, "textNew", item, user, createdTime);
        Comment actualComment = repository.saveAndFlush(comment);
        assertEquals(4L, actualComment.getId());
        assertEquals("textNew", actualComment.getText());
        assertEquals(1L, actualComment.getItem().getId());
        assertEquals(3L, actualComment.getAuthor().getId());
        assertEquals("2023-08-10 12:00:00", actualComment.getCreated().format(FORMATTER));
    }

}