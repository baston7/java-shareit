package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {
    private ItemService itemService;
    User user = new User("Вася", "b@mail.ru");
    User user2 = new User("Петя", "p@mail.ru");
    User user3 = new User("Сережа", "s@mail.ru");
    Item item = new Item(1, "ручка", "шариковая", Boolean.TRUE);
    Item item2 = new Item(1, null, "чернильное", Boolean.TRUE);
    Item item3 = new Item(3, "маркер", Boolean.TRUE);
    Item item4 = new Item(3, "карандаш", "мягкий", null);
    //текущая аренда
    Booking booking = new Booking(1, LocalDateTime.of(2022, 11, 1, 0, 0),
            LocalDateTime.of(2022, 11, 30, 0, 0), item, user2, Status.APPROVED);
    //будущая аренда
    Booking booking2 = new Booking(2, LocalDateTime.of(2023, 11, 1, 0, 0),
            LocalDateTime.of(2023, 12, 1, 0, 0), item, user, Status.APPROVED);
    //предыдущая аренда
    Booking booking3 = new Booking(3, LocalDateTime.of(2021, 11, 1, 0, 0),
            LocalDateTime.of(2021, 12, 1, 0, 0), item, user2, Status.APPROVED);
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;

    @BeforeEach
    public void createService() {
        itemService = new ItemService(itemRepository, bookingRepository,
                commentRepository);
    }

    @Test
    public void testFindItemWithoutItem() {
        assertThrows(ItemNotFoundException.class, () -> itemService
                .findItem(6));
    }

    @Test
    public void testFindUserItemsWithoutItems() {
        Mockito
                .when(itemRepository.findAllByOwnerIdOrderById(anyLong(), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());
        assertThrows(ItemNotFoundException.class, () -> itemService.findUserItems(1, 1, 1));
    }

    @Test
    public void testFindUserItemsWithItems() {
        item.setOwner(user3);
        Mockito
                .when(itemRepository.findAllByOwnerIdOrderById(3, PageRequest.of(0, 10)))
                .thenReturn(List.of(item));
        Mockito
                .when(commentRepository.findCommentsByItem_Id(anyLong()))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(bookingRepository
                        .findTopByItem_IdAndEndIsBeforeAndStatusIsOrderByEndDesc(anyLong(),
                                any(LocalDateTime.class), any(Status.class)))
                .thenReturn(Optional.of(booking3));
        Mockito
                .when(bookingRepository
                        .findTopByItem_IdAndStartIsAfterAndStatusIsNotAndStatusIsNotOrderByEndDesc(anyLong(),
                        any(LocalDateTime.class), any(Status.class), any(Status.class)))
                .thenReturn(Optional.of(booking2));

        assertEquals(1, itemService.findUserItems(3, 0, 10).size());
        assertEquals(booking2.getStart(), itemService.findUserItems(3, 0, 10).get(0)
                .getNextBooking().getStart());
        assertEquals(booking3.getStart(), itemService.findUserItems(3, 0, 10).get(0)
                .getLastBooking().getStart());
        assertEquals(item.getDescription(), itemService.findUserItems(3, 0, 10).get(0)
                .getDescription());
    }

    @Test
    public void testSetNewFieldsForUpdateWithEmptyNewItem() {
        assertThrows(ValidationException.class, () -> itemService.setNewFieldsForUpdate(new Item(), item));
    }

    @Test
    public void testSetNewFieldsForUpdateWithEmptyNameNewItem() {
        itemService.setNewFieldsForUpdate(item2, item);
        assertEquals("ручка", item2.getName());
        assertEquals("чернильное", item2.getDescription());
    }

    @Test
    public void testSetNewFieldsForUpdateWithEmptyDescriptionNewItem() {
        itemService.setNewFieldsForUpdate(item3, item);
        assertEquals("маркер", item3.getName());
        assertEquals("шариковая", item3.getDescription());
    }

    @Test
    public void testSetNewFieldsForUpdateWithNullAvailableNewItem() {
        itemService.setNewFieldsForUpdate(item4, item);
        assertEquals("карандаш", item4.getName());
        assertEquals(Boolean.TRUE, item4.getAvailable());
    }

    @Test
    public void testCheckUsersIdFromItems() {
        assertThrows(ItemNotFoundException.class, () -> itemService.checkUsersIdFromItems(1, 3));
    }

    @Test
    public void testAddCommentWithUserOwnerIsCommentOwner() {
        user.setId(1);
        item.setOwner(user);
        assertThrows(ValidationException.class, () -> itemService.addComment(user, item, "Хорошо"));
    }

    @Test
    public void testAddCommentWithCommentOwnerNotUseItem() {
        user.setId(1);
        user2.setId(2);
        item.setOwner(user);
        Mockito
                .when(bookingRepository
                        .findByBooker_IdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(anyLong(),
                                any(LocalDateTime.class),
                                any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        assertThrows(ValidationException.class, () -> itemService.addComment(user2, item, "Хорошо"));
    }

    @Test
    public void testAddCommentWithCommentOwnerUseItem() {
        user.setId(1);
        user2.setId(2);
        item.setOwner(user);
        List<Booking> bookings = List.of(booking3);
        Mockito
                .when(bookingRepository
                        .findByBooker_IdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(anyLong(),
                                any(LocalDateTime.class),
                                any(LocalDateTime.class)))
                .thenReturn(bookings);
        Mockito
                .when(commentRepository
                        .save(any(Comment.class)))
                .thenReturn(CommentMapper.toComment(user2, item, "Хорошо"));
        Comment comment = itemService.addComment(user2, item, "Хорошо");
        assertEquals("Хорошо", comment.getText());
        assertEquals(user2.getId(), comment.getAuthor().getId());
    }
}

