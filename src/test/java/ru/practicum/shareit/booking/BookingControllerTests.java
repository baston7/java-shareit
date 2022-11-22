package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoFromUser;
import ru.practicum.shareit.booking.dto.BookingDtoToUser;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToUser;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTests {
    @Mock
    private  BookingService bookingService;
    @Mock
    private  ItemService itemService;
    @Mock
    private  UserService userService;
    @InjectMocks
    private BookingController controller;
    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private MockMvc mvc;

    private ItemDto itemDto;
    private Item item;
    private BookingDtoFromUser dtoFromUser;
    private Item item2;
    private Item item3;
    private Item item4;
    private ItemDto newItemDto;
    private ItemDto itemDtoWithRequest;
    private User owner;
    private User booker;
    private ItemRequest itemRequest;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        itemDto = new ItemDto(1, "Ручка", "Гелевая", true, null);
        item = new Item(1, "Ручка", "Гелевая", true, null);
        item2 = new Item(2, "перо", "красивое", true, null);
        item3 = new Item(3, "машинка", "пишущая", true, null);
        item4 = new Item(4, "мышка", "компьютерная", true, null);
        newItemDto = new ItemDto(1, "Ручка", "Шариковая", true, null);
        itemDtoWithRequest = new ItemDto(2, "Карандаш", "черный", true, 1L);
        owner = new User(1, "Petya", "petya@mail.ru");
        booker = new User(3, "Vasia", "vasia@mail.ru");
        dtoFromUser=new BookingDtoFromUser(1L, LocalDateTime.of(2023, 11,
                1, 0, 0), LocalDateTime.of(2023, 12, 1, 0, 0));
    }
    @Test
    void testAddNewBooking() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(booker);
        when(itemService.findItem(1))
                .thenReturn(ItemMapper.toItem(itemDto, null));
        when(bookingService.addBooking(any(Booking.class),anyLong()))
                .thenReturn(BookingMapper.toBooking(dtoFromUser,booker, item));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(dtoFromUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.booker.id", is(3)));
    }
}
