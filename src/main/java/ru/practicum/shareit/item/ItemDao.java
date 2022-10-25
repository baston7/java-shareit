package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    Item create(Item item);

    Optional<Item> get(long id);

    List<Item> findAll();

    long update(Item item);

    List<Item> findAllUserItems(long userId);

    void delete(long id);
}
