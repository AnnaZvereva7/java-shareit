package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoForOwner;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemDtoWithDate {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoForOwner lastBooking;
    private BookingDtoForOwner nextBooking;
    private List<CommentDtoResponse> comments;

}
