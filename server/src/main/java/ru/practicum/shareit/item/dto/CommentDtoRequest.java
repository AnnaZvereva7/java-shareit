package ru.practicum.shareit.item.dto;

import lombok.*;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommentDtoRequest {
    private String text;
}
