package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Autowired
    public ItemService(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
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
        if(text.isBlank()){
            return Collections.emptyList();
        }
        return itemDao.findAll().stream()
                .filter(item -> (item.getName()
                        .toLowerCase()
                        .contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))&&item.getAvailable()==Boolean.TRUE)
                .collect(Collectors.toList());
    }
}
