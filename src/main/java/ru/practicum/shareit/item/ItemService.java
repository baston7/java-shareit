package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemDao itemDao;

    @Autowired
    public ItemService(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    public Item addItem(Item item) {
        return itemDao.create(item);
    }


    public Item updateItem(Item item) {
        itemDao.update(item);
        return findItem(item.getId());
    }

    public Item findItem(long itemId) {
        return itemDao.get(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
    }

    public List<Item> findUserItems(long userId) {
        return itemDao.findAllUserItems(userId);
    }

    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemDao.findAll().stream()
                .filter(item -> (item.getName()
                        .toLowerCase()
                        .contains(text.toLowerCase())
                        || item.getDescription().toLowerCase()
                        .contains(text.toLowerCase())) && item.getAvailable() == Boolean.TRUE)
                .collect(Collectors.toList());
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
}
