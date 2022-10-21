package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
public class BookingDto {
    @NotNull
    private long id;
    @PastOrPresent
    private LocalDateTime start;
    @PastOrPresent
    private LocalDateTime end;
    @NotNull
    private Item item;
    @NotNull
    private User booker;
    private Status status;
}

