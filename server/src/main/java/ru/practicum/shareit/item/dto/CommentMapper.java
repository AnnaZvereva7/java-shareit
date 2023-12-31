package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.users.model.User;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final Clock clock;

    public CommentDtoResponse toCommentDto(Comment comment) {
        return new CommentDtoResponse(comment.getId(),
                comment.getItem().getId(),
                comment.getText(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public Comment toComment(CommentDtoRequest commentDto, Item item, User author) {
        return new Comment(null,
                commentDto.getText(),
                item,
                author,
                LocalDateTime.now(clock));
    }
}
