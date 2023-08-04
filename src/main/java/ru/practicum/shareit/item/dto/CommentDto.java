package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;

public interface CommentDto {
    long getId();

    String getText();

    String getAuthorName();

    LocalDateTime getCreated();
}
