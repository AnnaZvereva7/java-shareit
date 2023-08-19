package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDtoImpl implements CommentDto {
    private long id;
    private long itemId;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
