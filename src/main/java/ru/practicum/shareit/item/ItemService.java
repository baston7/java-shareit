package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoToUser;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    public Item findItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
    }

    public List<ItemDtoToUser> findUserItems(long userId) {
        List<Item> ownerItems = itemRepository.findAllByOwnerIdOrderById(userId);
        if (ownerItems.isEmpty()) {
            throw new ItemNotFoundException("Не найдено вещей у пользователя");
        }

        List<ItemDtoToUser> ownerItemsAndBookings = ownerItems.stream()
                .map(item -> new ItemDtoToUser(item.getId(), item.getName(), item.getDescription(),
                        item.getAvailable(), null, null, getComments(item.getId())))
                .collect(Collectors.toList());
        ownerItemsAndBookings
                .forEach(item -> {
                    Optional<Booking> lastBooking = bookingRepository
                            .findTopByItem_IdAndEndIsBeforeAndStatusIsOrderByEndDesc(item.getId(),
                                    LocalDateTime.now(), Status.APPROVED);
                    Optional<Booking> nextBooking = bookingRepository
                            .findTopByItem_IdAndStartIsAfterAndStatusIsNotAndStatusIsNotOrderByEndDesc(item.getId(),
                                    LocalDateTime.now(), Status.CANCELED, Status.REJECTED);
                    nextBooking.ifPresent(booking -> item.setNextBooking(BookingMapper
                            .toBookingDtoToOwnerItemToUser(booking)));
                    lastBooking.ifPresent(booking -> item.setLastBooking(BookingMapper
                            .toBookingDtoToOwnerItemToUser(booking)));
                });
        return ownerItemsAndBookings;
    }

    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text);
    }

    public void setNewFieldsForUpdate(Item newItem, Item oldItem) {
        if (newItem.getName() == null && newItem.getDescription() == null && newItem.getAvailable() == null) {
            throw new ValidationException("Поля значений пустые");
        }
        if (newItem.getName() != null && newItem.getDescription() != null && newItem.getAvailable() != null) {
            return;
        }
        if (newItem.getName() == null) {
            newItem.setName(oldItem.getName());
        }
        if (newItem.getDescription() == null) {
            newItem.setDescription(oldItem.getDescription());
        }
        if (newItem.getAvailable() == null) {
            newItem.setAvailable(oldItem.getAvailable());
        }
    }

    public void checkUsersIdFromItems(long userIdOld, long userIdNew) {
        if (userIdOld != userIdNew) {
            throw new ItemNotFoundException("Вещь не найдена у данного пользователя");
        }
    }

    public Comment addComment(User user, Item item, String text) {
        if (user.getId() == item.getOwner().getId()) {
            throw new ValidationException("Пользователь не имеет право оставлять комментарий на свою вещь");
        }
        List<Booking> bookings = bookingRepository
                .findByBooker_IdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(user.getId(),
                        LocalDateTime.now(), LocalDateTime.now()).stream()
                .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь не имеет право оставлять комментарий");
        }

        return commentRepository.save(CommentMapper.toComment(user, item, text));
    }

    public List<CommentDto> getComments(long itemId) {
        List<Comment> comments = commentRepository.findCommentsByItem_Id(itemId);
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }
}
