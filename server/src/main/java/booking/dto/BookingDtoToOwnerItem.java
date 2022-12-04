package booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import booking.Status;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookingDtoToOwnerItem {
    private Long id;
    private long bookerId;
    private Status status;
    private LocalDateTime start;
    private LocalDateTime end;
}

