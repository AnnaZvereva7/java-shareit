package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDtoResponse {
    private Long id;
    private Long itemId;
    private String text;
    private Long authorId;
    private String authorName;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime created;
}
