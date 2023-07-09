package ru.practicum.shareit.item.dto;

public class ItemDto {
    private int id;
    private String name;
    private String description;
    private boolean isAvailable;

    public ItemDto(int id, String name, String description, boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
    }

}
