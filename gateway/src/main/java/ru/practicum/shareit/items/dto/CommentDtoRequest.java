package ru.practicum.shareit.items.dto;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommentDtoRequest {
    @NotBlank
    @Size(max = 256)
    private String text;
}
