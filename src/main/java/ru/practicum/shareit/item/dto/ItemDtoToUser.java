package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoToOwnerItem;

import java.util.List;


@Data
@AllArgsConstructor
public class ItemDtoToUser {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoToOwnerItem lastBooking;
    private BookingDtoToOwnerItem nextBooking;
    private List<CommentDto> comments;
}

