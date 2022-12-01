package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoFromUser;
import ru.practicum.shareit.booking.dto.BookingDtoToOwnerItem;
import ru.practicum.shareit.booking.dto.BookingDtoToUser;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoInBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
