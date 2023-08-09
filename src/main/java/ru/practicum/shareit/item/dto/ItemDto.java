package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.exception.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 50)
    private String name;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 256)
    private String description;
    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;
}
