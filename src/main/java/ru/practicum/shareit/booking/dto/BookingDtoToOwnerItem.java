package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingDtoToOwnerItem {
    private Long id;
    private long bookerId;
    private Status status;
    private LocalDateTime start;
    private LocalDateTime end;
}

