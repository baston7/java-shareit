package booking;

import booking.dto.BookingDtoToUser;
import booking.model.Booking;
import lombok.experimental.UtilityClass;
import booking.dto.BookingDtoFromUser;
import booking.dto.BookingDtoToOwnerItem;
import item.dto.ItemDtoInBookingDto;
import item.model.Item;
import user.model.User;

@UtilityClass
public class BookingMapper {
    public static BookingDtoToUser toBookingDtoToUser(Booking booking) {
        return new BookingDtoToUser(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new ItemDtoInBookingDto(booking.getItem().getId(), booking.getItem().getName(),
                        booking.getItem().getDescription(), booking.getItem().getAvailable()),
                new User(booking.getBooker().getId(), booking.getBooker().getName(), booking.getBooker().getEmail()),
                booking.getStatus()
        );
    }

    public static BookingDtoToOwnerItem toBookingDtoToOwnerItemToUser(Booking booking) {
        return new BookingDtoToOwnerItem(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStatus(),
                booking.getStart(),
                booking.getEnd()
        );
    }

    public static Booking toBooking(BookingDtoFromUser bookingDtoFromUser, User creator, Item item) {
        Booking booking = new Booking();
        booking.setBooker(creator);
        booking.setItem(item);
        booking.setStart(bookingDtoFromUser.getStart());
        booking.setEnd(bookingDtoFromUser.getEnd());
        booking.setStatus(Status.WAITING);
        return booking;
    }
}
