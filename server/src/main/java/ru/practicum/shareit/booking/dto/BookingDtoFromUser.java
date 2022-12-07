package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoFromUser {
    @NotNull(message = "Не указан id запрашиваемой вещи")
    private Long itemId;
    @FutureOrPresent(message = "Время начала аренды должно быть в настоящем или будущем времени")
    private LocalDateTime start;
    @Future(message = "Время конца аренды должно быть в будущем времени")
    private LocalDateTime end;
}

