package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
@Data
@AllArgsConstructor
public class Item {
    private int id;
    @NotBlank(groups = Marker.OnCreate.class)
    @With
    private final String name;
    @NotBlank(groups = Marker.OnCreate.class)
    @With
    private final String description;
    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;
    @With
    private final int ownerId;
    private Integer requestId;

}
