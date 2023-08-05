package ru.practicum.shareit.user.dto;

import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.Marker;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@Validated
@AllArgsConstructor
public class UserDto {
    @NotNull(groups = Marker.OnUpdate.class)
    private long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 50)
    private String name;
    @NotBlank(groups = Marker.OnCreate.class)
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @Size(max = 100)
    private String email;
}
