package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDtoToUser;
import ru.practicum.shareit.item.model.Item;

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
        if (ownerItems.isEmpty()){
            throw new ItemNotFoundException("Не найдено вещей у пользователя");
        }
        List<ItemDtoToUser> ownerItemsAndBookings = ownerItems.stream()
                .map(item -> new ItemDtoToUser(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), null, null))
                .collect(Collectors.toList());
        ownerItemsAndBookings
                .forEach(item -> {
                    Optional<Booking> lastBooking = bookingRepository.findTopByItem_IdAndEndIsBeforeAndStatusIsNotAndStatusIsNotOrderByEndDesc(item.getId(), LocalDateTime.now(), Status.CANCELED, Status.REJECTED);
                    Optional<Booking> nextBooking = bookingRepository.findTopByItem_IdAndEndIsAfterAndStatusIsNotAndStatusIsNotOrderByEndDesc(item.getId(), LocalDateTime.now(), Status.CANCELED, Status.REJECTED);
                    nextBooking.ifPresent(booking -> item.setNextBooking(BookingMapper.toBookingDtoToOwnerItemToUser(booking)));
                    lastBooking.ifPresent(booking -> item.setLastBooking(BookingMapper.toBookingDtoToOwnerItemToUser(booking)));
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
}
