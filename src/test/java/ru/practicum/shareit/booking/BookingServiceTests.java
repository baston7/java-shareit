package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeption.BookingNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class BookingServiceTests {
    private BookingService bookingService;
    User user = new User("Вася", "b@mail.ru");
    User user2 = new User("Петя", "p@mail.ru");
    User user3 = new User("Сережа", "s@mail.ru");
    Item item = new Item(1, "ручка", "шариковая", Boolean.TRUE);
    //текущая аренда
    Booking booking = new Booking(1, LocalDateTime.of(2021, 11, 1, 0, 0),
            LocalDateTime.of(2021, 11, 30, 0, 0), item, user2, Status.APPROVED);
    //not valid Booking
    Booking booking2 = new Booking(2, LocalDateTime.of(2025, 11, 1, 0, 0),
            LocalDateTime.of(2023, 12, 1, 0, 0), item, user, Status.APPROVED);
    //текущая аренда
    Booking booking3 = new Booking(3, LocalDateTime.of(2021, 11, 1, 0, 0),
            LocalDateTime.of(2021, 12, 1, 0, 0), item, user2, Status.APPROVED);
    //будущая аренда
    Booking booking5 = new Booking(3, LocalDateTime.of(2025, 11, 1, 0, 0),
            LocalDateTime.of(2025, 12, 1, 0, 0), item, user2, Status.WAITING);
    //прошлая аренда
    Booking booking6 = new Booking(3, LocalDateTime.of(2020, 11, 1, 0, 0),
            LocalDateTime.of(2020, 12, 1, 0, 0), item, user2, Status.APPROVED);
    //пересекающаяся аренда
    Booking booking4 = new Booking(3, LocalDateTime.of(2021, 10, 1, 0, 0),
            LocalDateTime.of(2021, 12, 1, 0, 0), item, user2, Status.APPROVED);
    @Mock
    BookingRepository bookingRepository;

    @BeforeEach
    public void createService() {
        bookingService = new BookingService(bookingRepository);
    }

    @Test
    public void testValidatorBookingWhereStartAfterEnd() {
        assertThrows(ValidationException.class, () -> bookingService.addBooking(booking2, 1));
    }

    @Test
    public void testValidatorBookingWhereOwnerEqualsCreator() {
        user2.setId(1);
        item.setOwner(user2);
        assertThrows(BookingNotFoundException.class, () -> bookingService.addBooking(booking, 1));
    }

    @Test
    public void testUpdateStatusBookingWhereNoBooking() {
        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class, () -> bookingService.updateStatusBooking(1L, 1L,
                true));
    }

    @Test
    public void testUpdateStatusBookingWhereUserIsNotOwner() {
        user2.setId(1);
        item.setOwner(user);
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        assertThrows(UserNotFoundException.class, () -> bookingService.updateStatusBooking(1L, user2.getId(),
                true));
    }

    @Test
    public void testUpdateStatusBookingWhereBookingApproved() {
        user2.setId(1);
        item.setOwner(user);
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        assertThrows(ValidationException.class, () -> bookingService.updateStatusBooking(1L, user.getId(),
                true));
    }

    @Test
    public void testUpdateStatusBookingWhereBookingWaitingAndApprovedTrue() {
        user2.setId(1);
        item.setOwner(user);
        booking.setStatus(Status.WAITING);
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(bookingRepository
                        .findByItem_Owner_IdAndEndIsAfterAndStatusIs(anyLong(), any(LocalDateTime.class),
                                any(Status.class)))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        Booking booking1 = bookingService.updateStatusBooking(1L, user.getId(), true);

        assertEquals(Status.APPROVED, booking1.getStatus());
    }

    @Test
    public void testUpdateStatusBookingWhereBookingWaitingAndApprovedTrueButOwnerApprovedAnotherBookingForThisTime() {
        user2.setId(1);
        item.setOwner(user);
        booking.setStatus(Status.WAITING);
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(bookingRepository
                        .findByItem_Owner_IdAndEndIsAfterAndStatusIs(anyLong(), any(LocalDateTime.class),
                                any(Status.class)))
                .thenReturn(List.of(booking4));
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        Booking booking1 = bookingService.updateStatusBooking(1L, user.getId(), true);

        assertEquals(Status.REJECTED, booking1.getStatus());
    }

    @Test
    public void testFindBookingByOwnerItemOrCreatorWhereNoBooking() {
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class, () -> bookingService.findBookingByOwnerItemOrCreator(1L,
                2L));
    }

    @Test
    public void testFindBookingByOwnerItemOrCreatorWhereUserNotOwnerItem() {
        user.setId(1);
        item.setOwner(user);
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        assertThrows(UserNotFoundException.class, () -> bookingService.findBookingByOwnerItemOrCreator(1L,
                3L));
    }

    @Test
    public void testFindBookingByOwnerItemOrCreatorWhereUserValid() {
        user.setId(1);
        item.setOwner(user);
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        Booking booking2 = bookingService.findBookingByOwnerItemOrCreator(1L, user.getId());
        assertEquals(booking.getStart(), booking2.getStart());
    }

    @Test
    public void testFindCreatorBookings() {
        user.setId(1);
        item.setOwner(user);
        user2.setId(2);
        Mockito
                .when(bookingRepository.findByBooker_IdOrderByEndDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking3, booking5, booking6));
        Mockito
                .when(bookingRepository
                        .findByBooker_IdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(anyLong(),
                                any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking6));
        Mockito
                .when(bookingRepository
                        .findByBooker_IdAndStartIsAfterAndEndIsAfterOrderByEndDesc(anyLong(),
                                any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking5));
        Mockito
                .when(bookingRepository
                        .findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(anyLong(),
                                any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking3));
        Mockito
                .when(bookingRepository.findByBooker_IdAndStatusEqualsOrderByEndDesc(2L, Status.WAITING,
                        PageRequest.of(0, 10)))
                .thenReturn(List.of(booking5));


        List<Booking> bookings = bookingService.findCreatorBookings(2L, "ALL", 0, 10);
        List<Booking> bookings1 = bookingService.findCreatorBookings(2L, "PAST", 0, 10);
        List<Booking> bookings2 = bookingService.findCreatorBookings(2L, "FUTURE", 0, 10);
        List<Booking> bookings3 = bookingService.findCreatorBookings(2L, "CURRENT", 0, 10);
        List<Booking> bookings4 = bookingService.findCreatorBookings(2L, "WAITING", 0, 10);
        assertEquals(3, bookings.size());
        assertEquals(1, bookings1.size());
        assertEquals(1, bookings2.size());
        assertEquals(1, bookings3.size());
        assertEquals(1, bookings4.size());
        assertEquals(booking6.getStart(), bookings1.get(0).getStart());
        assertEquals(booking5.getStart(), bookings2.get(0).getStart());
        assertEquals(booking3.getStart(), bookings3.get(0).getStart());
        assertEquals(Status.WAITING, bookings4.get(0).getStatus());
        assertThrows(ValidationException.class, () -> bookingService.findCreatorBookings(2L, "zzzzz",
                0, 10));
    }

    @Test
    public void testFindOwnerBookings() {
        user.setId(1);
        item.setOwner(user);
        user2.setId(2);
        Mockito
                .when(bookingRepository.findByItem_Owner_IdOrderByEndDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking3, booking5, booking6));
        Mockito
                .when(bookingRepository
                        .findByItem_Owner_IdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(anyLong(),
                                any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking6));
        Mockito
                .when(bookingRepository
                        .findByItem_Owner_IdAndStartIsAfterAndEndIsAfterOrderByEndDesc(anyLong(),
                                any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking5));
        Mockito
                .when(bookingRepository
                        .findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(anyLong(),
                                any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking3));
        Mockito
                .when(bookingRepository.findByItem_Owner_IdAndStatusEqualsOrderByEndDesc(1L, Status.WAITING,
                        PageRequest.of(0, 10)))
                .thenReturn(List.of(booking5));


        List<Booking> bookings = bookingService.findOwnerBookings(1L, "ALL", 0, 10);
        List<Booking> bookings1 = bookingService.findOwnerBookings(1L, "PAST", 0, 10);
        List<Booking> bookings2 = bookingService.findOwnerBookings(1L, "FUTURE", 0, 10);
        List<Booking> bookings3 = bookingService.findOwnerBookings(1L, "CURRENT", 0, 10);
        List<Booking> bookings4 = bookingService.findOwnerBookings(1L, "WAITING", 0, 10);
        assertEquals(3, bookings.size());
        assertEquals(1, bookings1.size());
        assertEquals(1, bookings2.size());
        assertEquals(1, bookings3.size());
        assertEquals(1, bookings4.size());
        assertEquals(booking6.getStart(), bookings1.get(0).getStart());
        assertEquals(booking5.getStart(), bookings2.get(0).getStart());
        assertEquals(booking3.getStart(), bookings3.get(0).getStart());
        assertEquals(Status.WAITING, bookings4.get(0).getStatus());
        assertThrows(ValidationException.class, () -> bookingService.findOwnerBookings(1L, "zzzzz",
                0, 10));
    }
}
