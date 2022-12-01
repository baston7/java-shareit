package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTests {
    @Mock
    private ItemService itemService;
    @Mock
    private ItemRequestService itemRequestService;
    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private Item item;
    private User requestor;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        item = new Item(1, "Ручка", "Гелевая", true, null);
        requestor = new User(2, "Vasia", "vasia@mail.ru");
        itemRequestDto = new ItemRequestDto();
    }

    @Test
    void testAddNewItemRequest() throws Exception {
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("Нужна ручка шариковая");
        when(userService.getUser(2))
                .thenReturn(requestor);
        when(itemRequestService.addRequest(any(ItemRequest.class)))
                .thenReturn(ItemRequestMapper.toItemRequest(itemRequestDto, requestor));


        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.description", is("Нужна ручка шариковая")))
                .andExpect(jsonPath("$.requestorId", is(2)));
    }

    @Test
    void testFindUserRequests() throws Exception {
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("Нужна ручка шариковая");
        when(userService.getUser(2))
                .thenReturn(requestor);
        when(itemRequestService.findUserRequests(2))
                .thenReturn(List.of(ItemRequestMapper.toItemRequest(itemRequestDto, requestor)));
        when(itemService.findItemsByRequest(anyLong()))
                .thenReturn(List.of(item));


        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].description", is("Нужна ручка шариковая")))
                .andExpect(jsonPath("$[0].requestorId", is(2)));
    }

    @Test
    void testFindAllRequests() throws Exception {
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("Нужна ручка шариковая");
        when(userService.getUser(2))
                .thenReturn(requestor);
        when(itemRequestService.findAllRequests(2, 0, 1))
                .thenReturn(List.of(ItemRequestMapper.toItemRequest(itemRequestDto, requestor)));
        when(itemService.findItemsByRequest(anyLong()))
                .thenReturn(List.of(item));


        mvc.perform(get("/requests/all/?from=0&size=1")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].description", is("Нужна ручка шариковая")))
                .andExpect(jsonPath("$[0].requestorId", is(2)));
    }

    @Test
    void testFindUserRequest() throws Exception {
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("Нужна ручка шариковая");
        when(userService.getUser(2))
                .thenReturn(requestor);
        when(itemService.findItemsByRequest(anyLong()))
                .thenReturn(List.of(item));
        when(itemRequestService.findOneRequest(1))
                .thenReturn(ItemRequestMapper.toItemRequest(itemRequestDto, requestor));


        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.description", is("Нужна ручка шариковая")))
                .andExpect(jsonPath("$.requestorId", is(2)));
    }

}
