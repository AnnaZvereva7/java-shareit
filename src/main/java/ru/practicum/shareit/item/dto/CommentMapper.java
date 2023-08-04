package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    private final ItemService itemService;
    private final UserService userService;

    public CommentMapper(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    public CommentDtoClass toCommentDto(Comment comment) {
        return new CommentDtoClass(comment.getId(),
                comment.getText(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public Comment toComment(CommentDtoClass commentDto, long itemId) {
        return new Comment(null,
                commentDto.getText(),
                itemService.findById(itemId),
                userService.findById(commentDto.getAuthorId()),
                LocalDateTime.now());
    }
}
