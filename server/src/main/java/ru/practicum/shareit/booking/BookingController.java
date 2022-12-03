package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoFromUser;
import ru.practicum.shareit.booking.dto.BookingDtoToUser;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private static  final String HEADER_NAME = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoToUser addBooking(@RequestHeader(HEADER_NAME) long creatorId,
                                       @RequestBody @Valid BookingDtoFromUser bookingDtoFromUser) {
        User creator = userService.getUser(creatorId);
        Item item = itemService.findItem(bookingDtoFromUser.getItemId());
        itemService.checkAvailable(item);
        Booking booking = BookingMapper.toBooking(bookingDtoFromUser, creator, item);
        return BookingMapper.toBookingDtoToUser(bookingService.addBooking(booking, creatorId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoToUser updateStatusBooking(@RequestHeader(HEADER_NAME) long ownerId, @PathVariable long bookingId,
                                                @RequestParam boolean approved) {
        userService.getUser(ownerId);
        Booking booking = bookingService.updateStatusBooking(bookingId, ownerId, approved);
        return BookingMapper.toBookingDtoToUser(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoToUser findBooking(@RequestHeader(HEADER_NAME) long userId, @PathVariable long bookingId) {
        userService.getUser(userId);
        Booking booking = bookingService.findBookingByOwnerItemOrCreator(bookingId, userId);
        return BookingMapper.toBookingDtoToUser(booking);
    }

    @GetMapping
    public List<BookingDtoToUser> findBookingsCreator(@RequestHeader(HEADER_NAME) long creatorId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0") @Min(0) int from,
                                                      @RequestParam(defaultValue = "10") @Min(1) int size) {
        userService.getUser(creatorId);
        return bookingService.findCreatorBookings(creatorId, state, from / size, size).stream()
                .map(BookingMapper::toBookingDtoToUser)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDtoToUser> findOwnerBookings(@RequestHeader(HEADER_NAME) long ownerId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(defaultValue = "0") @Min(0) int from,
                                                    @RequestParam(defaultValue = "10") @Min(1) int size) {
        userService.getUser(ownerId);
        itemService.findUserItems(ownerId, 0, size);
        return bookingService.findOwnerBookings(ownerId, state, from / size, size).stream()
                .map(BookingMapper::toBookingDtoToUser)
                .collect(Collectors.toList());
    }
}
