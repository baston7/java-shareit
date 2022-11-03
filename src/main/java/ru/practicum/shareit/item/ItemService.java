package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    public Item findItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
    }

    public List<Item> findUserItems(long userId) {
        return itemRepository.findAllByOwnerId(userId);
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
