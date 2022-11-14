package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoFromUser;
import ru.practicum.shareit.booking.dto.BookingDtoToUser;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final String headerName = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoToUser addBooking(@RequestHeader(headerName) long creatorId,
                                       @RequestBody @Valid BookingDtoFromUser bookingDtoFromUser) {
        User creator = userService.getUser(creatorId);
        Item item = itemService.findItem(bookingDtoFromUser.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна ");
        }

        Booking booking = BookingMapper.toBooking(bookingDtoFromUser, creator, item);
        return BookingMapper.toBookingDtoToUser(bookingService.addBooking(booking, creatorId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoToUser updateStatusBooking(@RequestHeader(headerName) long ownerId, @PathVariable long bookingId,
                                                @RequestParam boolean approved) {
        userService.getUser(ownerId);
        Booking booking = bookingService.updateStatusBooking(bookingId, ownerId, approved);
        return BookingMapper.toBookingDtoToUser(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoToUser findBooking(@RequestHeader(headerName) long userId, @PathVariable long bookingId) {
        userService.getUser(userId);
        Booking booking = bookingService.findBookingByOwnerItemOrCreator(bookingId, userId);
        return BookingMapper.toBookingDtoToUser(booking);
    }

    @GetMapping
    public List<BookingDtoToUser> findBookingsCreator(@RequestHeader(headerName) long creatorId,
                                                      @RequestParam(defaultValue = "ALL") String state) {
        userService.getUser(creatorId);

        return bookingService.findCreatorBookings(creatorId, state).stream()
                .map(BookingMapper::toBookingDtoToUser)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDtoToUser> findOwnerBookings(@RequestHeader(headerName) long ownerId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        userService.getUser(ownerId);
        if (itemService.findUserItems(ownerId).isEmpty()) {
            throw new ItemNotFoundException("У пользователя нет вещей");
        }

        return bookingService.findOwnerBookings(ownerId, state).stream()
                .map(BookingMapper::toBookingDtoToUser)
                .collect(Collectors.toList());
    }
}
