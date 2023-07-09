package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    private final int id;
    private final String description;
    private final int requestorId;
    private final LocalDate created;

}
