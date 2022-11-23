package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDtoInBookingDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingDtoToUser {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoInBookingDto item;
    private User booker;
    private Status status;
}
