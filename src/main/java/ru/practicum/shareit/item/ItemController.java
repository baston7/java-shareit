package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToUser;
import ru.practicum.shareit.item.model.Comment;
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
    private final String headerName = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@RequestHeader(headerName) long userId, @RequestBody @Valid ItemDto itemDto) {
        User user = userService.getUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemService.addItem(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(headerName) long userId, @PathVariable long itemId,
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
    public ItemDtoToUser findItem(@RequestHeader(headerName) long userId, @PathVariable long itemId) {
        userService.getUser(userId);
        if (itemService.findItem(itemId).getOwner().getId() != userId) {
            return ItemMapper.toItemDtoToUser(itemService.findItem(itemId), itemService.getComments(itemId));
        } else {
            return itemService.findUserItems(userId).stream()
                    .filter(itemDtoToUser -> itemDtoToUser.getId() == itemId)
                    .findFirst().orElseThrow(() -> new ItemNotFoundException("Не найдено вещей у пользователя"));
        }
    }

    @GetMapping
    public List<ItemDtoToUser> findUserItems(@RequestHeader(headerName) long userId) {
        userService.getUser(userId);
        return itemService.findUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(headerName) long userId, @RequestParam String text) {
        userService.getUser(userId);
        return itemService.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(headerName) long userId, @PathVariable long itemId,
                                 @RequestBody @Valid CommentDto commentDto) {
        User user = userService.getUser(userId);
        Item item = itemService.findItem(itemId);
        String text = commentDto.getText();
        Comment comment = itemService.addComment(user, item, text);
        return CommentMapper.toCommentDto(comment);
    }

}
