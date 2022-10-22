package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemDto itemDto) {
        User user = userService.getUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemService.addItem(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        User user = userService.getUser(userId);
        Item oldItem = itemService.findItem(itemId);
        if (oldItem.getOwner().getId()!=userId){
            throw new ItemNotFoundException("Данный пользователь не может редактировать вещь");
        }
        itemDto.setId(itemId);
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(user);
        if (newItem.getName() == null && newItem.getDescription() == null && newItem.getAvailable() == null) {
            throw new ValidationException("Поля значений пустые");
        }
        if (newItem.getName() != null && newItem.getDescription() != null && newItem.getAvailable() != null) {
            return ItemMapper.toItemDto(itemService.updateItem(newItem));
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
        return ItemMapper.toItemDto(itemService.updateItem(newItem));
    }

    @GetMapping("/{itemId}")
    public ItemDto findItem(@PathVariable long itemId) {
        return ItemMapper.toItemDto(itemService.findItem(itemId));
    }

    @GetMapping
    public List<ItemDto> findUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        userService.getUser(userId);
        return itemService.findUserItems(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text) {
        userService.getUser(userId);
        return itemService.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
