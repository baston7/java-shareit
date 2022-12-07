package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToUser;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTests {
    @Mock
    private ItemService itemService;
    @Mock
    private ItemRequestService itemRequestService;
    @Mock
    private UserService userService;

    @InjectMocks
    private ItemController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private ItemDto itemDto;
    private Item item;
    private Item item2;
    private Item item3;
    private Item item4;
    private ItemDto newItemDto;
    private ItemDto itemDtoWithRequest;
    private User owner;
    private User requestor;
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
        requestor = new User(2, "Vasia", "vasia@mail.ru");
        itemRequest = new ItemRequest(1, "нужна ручка гелевая", requestor, LocalDateTime.now());
        commentDto = new CommentDto(1, "Прекрасно", 1, requestor.getName());
    }

    @Test
    void testAddNewItemWithoutRequest() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(itemService.addItem(any(Item.class)))
                .thenReturn(ItemMapper.toItem(itemDto, null));


        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name", is("Ручка")))
                .andExpect(jsonPath("$.description", is("Гелевая")))
                .andExpect(jsonPath("$.requestId", is(nullValue())));
    }

    @Test
    void testAddNewItemWitRequest() throws Exception {

        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(itemRequestService.findOneRequest(anyLong()))
                .thenReturn(itemRequest);
        when(itemService.addItem(any(Item.class)))
                .thenReturn(ItemMapper.toItem(itemDtoWithRequest, itemRequest));


        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDtoWithRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name", is("Карандаш")))
                .andExpect(jsonPath("$.description", is("черный")))
                .andExpect(jsonPath("$.requestId", is(1)));
    }

    @Test
    void testUpdateItem() throws Exception {
        item.setOwner(owner);
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(itemService.findItem(anyLong()))
                .thenReturn(item);
        doNothing().when(itemService).checkUsersIdFromItems(anyLong(), anyLong());
        doNothing().when(itemService).setNewFieldsForUpdate(any(Item.class), any(Item.class));
        when(itemService.updateItem(any(Item.class)))
                .thenReturn(ItemMapper.toItem(newItemDto, null));


        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(newItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name", is("Ручка")))
                .andExpect(jsonPath("$.description", is("Шариковая")))
                .andExpect(jsonPath("$.requestId", is(nullValue())));
    }

    @Test
    void testFindItem() throws Exception {
        List<Item> userItems = List.of(item, item2, item3, item4);
        userItems.forEach(item1 -> item1.setOwner(owner));
        List<ItemDtoToUser> userItemsDto = userItems.stream()
                .map(item1 -> ItemMapper.toItemDtoToUser(item1, Collections.emptyList()))
                .collect(Collectors.toList());
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(itemService.findItem(anyLong()))
                .thenReturn(item);
        when(itemService.findUserItems(anyLong(), any(Integer.class), any(Integer.class)))
                .thenReturn(userItemsDto);


        mvc.perform(get("/items/2")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name", is("перо")))
                .andExpect(jsonPath("$.description", is("красивое")));
    }

    @Test
    void testFindUserItems() throws Exception {
        List<Item> userItems = List.of(item, item2, item3, item4);
        userItems.forEach(item1 -> item1.setOwner(owner));
        List<ItemDtoToUser> userItemsDto = userItems.stream()
                .map(item1 -> ItemMapper.toItemDtoToUser(item1, Collections.emptyList()))
                .collect(Collectors.toList());
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(itemService.findUserItems(1, 1, 1))
                .thenReturn(List.of(userItemsDto.get(1)));


        mvc.perform(get("/items/?from=1&size=1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].name", is("перо")))
                .andExpect(jsonPath("$[0].description", is("красивое")));
    }

    @Test
    void testSearchItems() throws Exception {
        List<Item> userItems = List.of(item, item2, item3, item4);
        userItems.forEach(item1 -> item1.setOwner(owner));
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(itemService.searchItems("перо", 1, 1))
                .thenReturn(List.of(userItems.get(1)));


        mvc.perform(get("/items/search?text=перо&from=1&size=1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].name", is("перо")))
                .andExpect(jsonPath("$[0].description", is("красивое")));
    }

    @Test
    void testAddComment() throws Exception {
        List<Item> userItems = List.of(item, item2, item3, item4);
        userItems.forEach(item1 -> item1.setOwner(owner));
        when(userService.getUser(anyLong()))
                .thenReturn(requestor);
        when(itemService.findItem(1))
                .thenReturn(item);
        when(itemService.addComment(any(User.class), any(Item.class), anyString()))
                .thenReturn(CommentMapper.toComment(requestor, item, "прекрасно"));


        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.text", is("прекрасно")))
                .andExpect(jsonPath("$.itemId", is(1)));
    }
}
