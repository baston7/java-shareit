package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeption.BookingNotFoundException;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.Collections;
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
    Item item2 = new Item(1, null, "чернильное", Boolean.TRUE);
    Item item3 = new Item(3, "маркер", null, Boolean.TRUE);
    Item item4 = new Item(3, "карандаш", "мягкий", null);
    //текущая аренда
    Booking booking = new Booking(1, LocalDateTime.of(2021, 11, 1, 0, 0),
            LocalDateTime.of(2021, 11, 30, 0, 0), item, user2, Status.APPROVED);
    //будущая аренда
    Booking booking2 = new Booking(2, LocalDateTime.of(2025, 11, 1, 0, 0),
            LocalDateTime.of(2023, 12, 1, 0, 0), item, user, Status.APPROVED);
    //предыдущая аренда
    Booking booking3 = new Booking(3, LocalDateTime.of(2021, 11, 1, 0, 0),
            LocalDateTime.of(2021, 12, 1, 0, 0), item, user2, Status.APPROVED);
    @Mock
    BookingRepository bookingRepository;

    @BeforeEach
    public void createService() {
        bookingService = new BookingService(bookingRepository);
    }
    @Test
    public void testValidatorBookingWhereStartAfterEnd(){
        assertThrows(ValidationException.class,()->bookingService.addBooking(booking2,1));
    }
    @Test
    public void testValidatorBookingWhereOwnerEqualsCreator(){
        user2.setId(1);
        item.setOwner(user2);
        assertThrows(BookingNotFoundException.class,()->bookingService.addBooking(booking,1));
    }
    @Test
    public void testUpdateStatusBookingWhereNoBooking(){
        Mockito
                .when(bookingRepository.findById(anyLong()))
                                .thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class,()->bookingService.updateStatusBooking(1L,1L,true));
    }
    @Test
    public void testUpdateStatusBookingWhereUserIsNotOwner(){
        user2.setId(1);
        item.setOwner(user);
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        assertThrows(UserNotFoundException.class,()->bookingService.updateStatusBooking(1L,user2.getId(),true));
    }
    @Test
    public void testUpdateStatusBookingWhereBookingApproved(){
        user2.setId(1);
        item.setOwner(user);
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        assertThrows(ValidationException.class,()->bookingService.updateStatusBooking(1L,user.getId(),true));
    }
    @Test
    public void testUpdateStatusBookingWhereBookingWaitingAndApprovedTrue(){
        user2.setId(1);
        item.setOwner(user);
        booking.setStatus(Status.WAITING);
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(bookingRepository
                        .findByItem_Owner_IdAndEndIsAfterAndStatusIs(anyLong(),any(LocalDateTime.class),any(Status.class)))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

       Booking booking1=bookingService.updateStatusBooking(1L,user.getId(),true);

       assertEquals(Status.APPROVED,booking1.getStatus());
    }
    @Test
    public void testUpdateStatusBookingWhereBookingWaitingAndApprovedTrueButOwnerApprovedAnotherBookingForThisTime(){
        user2.setId(1);
        item.setOwner(user);
        booking.setStatus(Status.WAITING);
        Mockito
                .when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        Mockito
                .when(bookingRepository
                        .findByItem_Owner_IdAndEndIsAfterAndStatusIs(anyLong(),any(LocalDateTime.class),any(Status.class)))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        Booking booking1=bookingService.updateStatusBooking(1L,user.getId(),true);

        assertEquals(Status.APPROVED,booking1.getStatus());
    }
}
