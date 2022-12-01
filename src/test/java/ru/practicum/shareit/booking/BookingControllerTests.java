package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDtoFromUser;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTests {
    @Mock
    private BookingService bookingService;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @InjectMocks
    private BookingController controller;
    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private MockMvc mvc;

    private ItemDto itemDto;
    private Item item;
    private BookingDtoFromUser dtoFromUser;
    private Booking booking;
    private User owner;
    private User booker;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        itemDto = new ItemDto(1, "Ручка", "Гелевая", true, null);
        item = new Item(1, "Ручка", "Гелевая", true, null);
        owner = new User(1, "Petya", "petya@mail.ru");
        booker = new User(3, "Vasia", "vasia@mail.ru");
        dtoFromUser = new BookingDtoFromUser(1L, LocalDateTime.of(2023, 11,
                1, 0, 0), LocalDateTime.of(2023, 12, 1, 0, 0));
        booking = new Booking(1L, LocalDateTime.of(2023, 11,
                1, 0, 0), LocalDateTime.of(2023, 12, 1, 0, 0), item, booker, Status.APPROVED);
    }

    @Test
    void testAddNewBooking() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(booker);
        when(itemService.findItem(1))
                .thenReturn(ItemMapper.toItem(itemDto, null));
        when(bookingService.addBooking(any(Booking.class), anyLong()))
                .thenReturn(BookingMapper.toBooking(dtoFromUser, booker, item));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(dtoFromUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.booker.id", is(3)))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void testUpdateStatusBooking() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(bookingService.updateStatusBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/1/?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void testFindBooking() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(bookingService.findBookingByOwnerItemOrCreator(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void testFindBookingsCreator() throws Exception {
        when(userService.getUser(3))
                .thenReturn(booker);
        when(bookingService.findCreatorBookings(3L, "FUTURE", 0, 1))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/?state=FUTURE&from=0&size=1")
                        .header("X-Sharer-User-Id", 3)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].status", is("APPROVED")));
    }

    @Test
    void testFindBookingsOwner() throws Exception {
        when(userService.getUser(1))
                .thenReturn(owner);
        when(itemService.findUserItems(1L, 0, 1))
                .thenReturn(List.of(ItemMapper.toItemDtoToUser(item, Collections.emptyList())));
        when(bookingService.findOwnerBookings(1L, "FUTURE", 0, 1))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner/?state=FUTURE&from=0&size=1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.[0].status", is("APPROVED")));
    }
}
