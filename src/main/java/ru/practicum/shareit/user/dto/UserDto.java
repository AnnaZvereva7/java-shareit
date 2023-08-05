package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
