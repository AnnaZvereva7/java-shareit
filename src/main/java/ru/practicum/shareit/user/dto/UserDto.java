package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
@AllArgsConstructor
public class UserDto {
    @NotNull(groups = Marker.OnUpdate.class)
    private long id;
    @NotBlank(groups = Marker.OnCreate.class)
    private String name;
    @NotNull(groups = Marker.OnCreate.class)
    @Email
    private String email;
}
