package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDtoInBookingDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoToUser {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    ItemDtoInBookingDto item;
    User booker;
    Status status;
}
