package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemDaoImpl implements ItemDao {

    private List<Item> itemList = new ArrayList<>();
    private long id = 1;

    @Override
    public Item create(Item item) {
        item.setId(id);
        id++;
        itemList.add(item);
        return item;
    }

    @Override
    public Optional<Item> get(long id) {
        return itemList.stream()
                .filter(item -> item.getId() == id)
                .findFirst();
    }

    @Override
    public List<Item> findAll() {
        return itemList;
    }

    @Override
    public long update(Item newItem) {
        Item oldItem = get(newItem.getId()).orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        oldItem.setName(newItem.getName());
        oldItem.setDescription(newItem.getDescription());
        oldItem.setAvailable(newItem.getAvailable());
        oldItem.setOwner(newItem.getOwner());
        oldItem.setRequest(newItem.getRequest());
        return oldItem.getId();
    }

    @Override
    public List<Item> findAllUserItems(long userId) {
        return itemList.stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        itemList.remove(get(id).orElseThrow(() -> new ItemNotFoundException("Не найдена вещь на удаление")));
    }
}
