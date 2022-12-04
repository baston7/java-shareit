package item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import booking.dto.BookingDtoToOwnerItem;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemDtoToUser {
    long id;
    String name;
    String description;
    Boolean available;
    BookingDtoToOwnerItem lastBooking;
    BookingDtoToOwnerItem nextBooking;
    List<CommentDto> comments;
}

