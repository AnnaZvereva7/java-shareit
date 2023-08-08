package ru.practicum.shareit.users.dto;

import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {
    private long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 50, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String name;
    @NotBlank(groups = Marker.OnCreate.class)
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @Size(max = 100, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String email;
}
