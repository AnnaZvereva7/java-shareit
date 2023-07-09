package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.With;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
public class User {
    @NotNull(groups = Marker.OnUpdate.class)
    @With
    private final int id;
    @NotBlank(groups = Marker.OnCreate.class)
    @With
    private final String name;
    @NotNull(groups = Marker.OnCreate.class)
    @Email
    @With
    private final String email; //уникальный адрес
}
