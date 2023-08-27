package ru.practicum.shareit.users;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static ru.practicum.shareit.common.Marker.OnCreate;
import static ru.practicum.shareit.common.Marker.OnUpdate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {
    private Long id;
    @NotBlank(groups = OnCreate.class)
    @Size(max = 50, groups = {OnCreate.class, OnUpdate.class})
    private String name;
    @NotBlank(groups = OnCreate.class)
    @Email(groups = {OnCreate.class, OnUpdate.class})
    @Size(max = 100, groups = {OnCreate.class, OnUpdate.class})
    private String email;
}
