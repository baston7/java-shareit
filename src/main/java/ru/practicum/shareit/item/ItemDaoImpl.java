package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemDaoImpl implements ItemDao {

    private static List<Item> itemList = new ArrayList<>();
    private static long id = 1;

    @Override
    public Item create(Item item) {
        item.setId(id);
        id++;
        itemList.add(item);
        return item;
    }

    @Override
    public Optional<Item> get(long id) {
        return itemList.stream().filter(item -> item.getId() == id).findFirst();
    }

    @Override
    public List<Item> findAll() {
        return itemList;
    }

    @Override
    public long update(Item newItem) {
        Item oldItem = get(newItem.getId()).get();
        oldItem.setName(newItem.getName());
        oldItem.setDescription(newItem.getDescription());
        oldItem.setAvailable(newItem.getAvailable());
        oldItem.setOwner(newItem.getOwner());
        oldItem.setRequest(newItem.getRequest());
        return oldItem.getId();
    }

    @Override
    public List<Item> findAllUserItems(long userId) {
        return itemList.stream().filter(item -> item.getOwner().getId()==userId).collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        itemList.remove(get(id).get());
    }
}
