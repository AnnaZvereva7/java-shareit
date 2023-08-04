package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForOwner;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithDate {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingForOwner lastBooking;
    private BookingForOwner nextBooking;
    private List<CommentDto> comments;

}
