package ru.practicum.shareit.users.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {
    private long id;
    private String name;
    private String email;
}
