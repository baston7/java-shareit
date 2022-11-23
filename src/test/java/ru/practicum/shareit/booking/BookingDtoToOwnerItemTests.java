package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDtoToOwnerItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class BookingDtoToOwnerItemTests {
    private BookingDtoToOwnerItem bookingDtoToOwnerItem;

    @Test
    public void testMappingDto() {
        Item item = new Item(1, "Ручка", "Гелевая", true, null);
        User booker = new User(3, "Vasia", "vasia@mail.ru");
        Booking booking = new Booking(1L, LocalDateTime.of(2023, 11,
                1, 0, 0), LocalDateTime.of(2023, 12, 1, 0,
                0), item, booker, Status.APPROVED);
        bookingDtoToOwnerItem = BookingMapper.toBookingDtoToOwnerItemToUser(booking);
        assertEquals(1, bookingDtoToOwnerItem.getId());
        assertEquals(3, bookingDtoToOwnerItem.getBookerId());
        assertEquals(Status.APPROVED, bookingDtoToOwnerItem.getStatus());
    }
}
