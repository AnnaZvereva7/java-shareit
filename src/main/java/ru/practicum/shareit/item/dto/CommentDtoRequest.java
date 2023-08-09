package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
@Getter
@Setter
@NoArgsConstructor
public class CommentDtoRequest {
    @NotBlank
    @Size(max = 256)
    private String text;
}
