package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Validated
@Data
@AllArgsConstructor
public class CommentDtoClass {
    private long id;
    @NotNull
    @NotBlank
    private String text;
    private long author_id;
    private String authorName;
    private LocalDateTime created;
}
