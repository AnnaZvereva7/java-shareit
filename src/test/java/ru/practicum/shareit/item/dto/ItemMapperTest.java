package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
class ItemMapperTest {
    private ItemMapper mapper = new ItemMapper();

    @Test
    void toItemDto() {
        Item item = new Item(1L, "name", "description", true, null, null);
        ItemDto itemDto = mapper.toItemDto(item);

        assertEquals(1L, itemDto.getId());
        assertEquals("name", itemDto.getName());
        assertEquals("description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
        assertEquals(null, itemDto.getRequestId());
    }

    @Test
    void fromItemDto() {
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L);
        Item item = mapper.fromItemDto(itemDto);
        assertEquals(1L, item.getId());
        assertEquals("name", item.getName());
        assertEquals("description", item.getDescription());
        assertEquals(true, item.getAvailable());
        assertEquals(1L, item.getRequestId());
        assertEquals(null, item.getOwnerId());
    }

    @Test
    void toItemDtoWithDate() {
        Item item = new Item(1L, "name", "description", true, null, null);
        ItemDtoWithDate itemDtoWithDate = mapper.toItemDtoWithDate(item);
        assertEquals(1L, itemDtoWithDate.getId());
        assertEquals("name", itemDtoWithDate.getName());
        assertEquals("description", itemDtoWithDate.getDescription());
        assertEquals(true, itemDtoWithDate.getAvailable());
        assertEquals(null, itemDtoWithDate.getLastBooking());
        assertEquals(null, itemDtoWithDate.getNextBooking());
        assertEquals(null, itemDtoWithDate.getComments());
    }
}