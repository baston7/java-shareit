package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoFromUser;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTests {
    @Mock
    private BookingClient bookingClient;
    @InjectMocks
    private BookingController controller;
    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private MockMvc mvc;
    private BookingDtoFromUser dtoFromUser;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        dtoFromUser = new BookingDtoFromUser(1L, LocalDateTime.of(2023, 11,
                1, 0, 0), LocalDateTime.of(2023, 12, 1, 0, 0));
    }

    @Test
    void testAddNewInvalidBooking() throws Exception {
        dtoFromUser.setItemId(null);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(dtoFromUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void testAddNewValidBooking() throws Exception {
        mvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", 3)
                .content(mapper.writeValueAsString(dtoFromUser))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        Mockito.verify(bookingClient, Mockito.times(1))
                .bookItem(anyLong(), any(BookingDtoFromUser.class));
    }
}
