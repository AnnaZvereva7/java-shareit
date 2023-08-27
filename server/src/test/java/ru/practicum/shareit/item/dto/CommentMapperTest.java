package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.users.model.User;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;


class CommentMapperTest {
    private CommentMapper mapper;
    private Clock clock;

    @BeforeEach
    public void before() {
        clock = Clock.fixed(
                Instant.parse("2023-08-10T12:00:00.00Z"),
                ZoneId.of("UTC"));
        mapper = new CommentMapper(clock);
    }

    @Test
    void toCommentDto() {
        LocalDateTime createdTime = LocalDateTime.of(2023, 8, 10, 12, 0);
        Item item = new Item(1L, "name", "Description", true, null);
        User user = new User(3L, "name3", "email3@mail.ru");
        Comment comment = new Comment(1L, "textNew", item, user, createdTime);
        CommentDtoResponse commentDtoResponse = mapper.toCommentDto(comment);
        assertEquals(1L, commentDtoResponse.getId());
        assertEquals("textNew", commentDtoResponse.getText());
        assertEquals(3L, commentDtoResponse.getAuthorId());
        assertEquals("name3", commentDtoResponse.getAuthorName());
        assertEquals(createdTime, commentDtoResponse.getCreated());
    }

    @Test
    void toComment() {
        LocalDateTime createdTime = LocalDateTime.of(2023, 8, 10, 12, 0);
        Item item = new Item(1L, "name", "Description", true, null);
        User user = new User(3L, "name3", "email3@mail.ru");
        CommentDtoRequest commentDto = new CommentDtoRequest("text");
        Comment actualComment = mapper.toComment(commentDto, item, user);

        assertEquals(null, actualComment.getId());
        assertEquals("text", actualComment.getText());
        assertEquals(item, actualComment.getItem());
        assertEquals(user, actualComment.getAuthor());
        assertEquals(LocalDateTime.now(clock), actualComment.getCreated());
    }
}