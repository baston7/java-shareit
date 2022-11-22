package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoFromUser {
    @NotNull(message = "Не указан id запрашиваемой вещи")
    Long itemId;
    @FutureOrPresent(message = "Время начала аренды должно быть в настоящем или будущем времени")
    LocalDateTime start;
    @Future(message = "Время конца аренды должно быть в будущем времени")
    LocalDateTime end;
}

