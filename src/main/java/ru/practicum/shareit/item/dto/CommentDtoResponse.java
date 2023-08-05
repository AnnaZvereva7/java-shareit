package ru.practicum.shareit.item.dto;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDtoResponse {
    private long id;
    @NotBlank
    @Size(max = 256)
    private String text;
    private long authorId;
    private String authorName;
    private LocalDateTime created;
}
