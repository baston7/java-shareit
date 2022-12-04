package booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import booking.Status;
import item.dto.ItemDtoInBookingDto;
import user.model.User;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookingDtoToUser {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoInBookingDto item;
    private User booker;
    private Status status;
}
