package ru.practicum.shareit.items.dto;

import lombok.*;
import ru.practicum.shareit.common.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 50, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String name;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 256, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String description;
    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;
    private Long requestId;
}
