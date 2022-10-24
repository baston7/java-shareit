package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final String HEADER_NAME = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@RequestHeader(HEADER_NAME) long userId, @RequestBody @Valid ItemDto itemDto) {
        User user = userService.getUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemService.addItem(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_NAME) long userId, @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        User user = userService.getUser(userId);
        Item oldItem = itemService.findItem(itemId);
        itemService.checkUsersIdFromItems(oldItem.getOwner().getId(), userId);
        itemDto.setId(itemId);
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(user);
        itemService.setNewFieldsForUpdate(newItem, oldItem);
        return ItemMapper.toItemDto(itemService.updateItem(newItem));
    }

    @GetMapping("/{itemId}")
    public ItemDto findItem(@RequestHeader(HEADER_NAME) long userId, @PathVariable long itemId) {
        userService.getUser(userId);
        return ItemMapper.toItemDto(itemService.findItem(itemId));
    }

    @GetMapping
    public List<ItemDto> findUserItems(@RequestHeader(HEADER_NAME) long userId) {
        userService.getUser(userId);
        return itemService.findUserItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(HEADER_NAME) long userId, @RequestParam String text) {
        userService.getUser(userId);
        return itemService.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
