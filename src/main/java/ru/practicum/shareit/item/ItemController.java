package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToUser;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final String headerName = "X-Sharer-User-Id";

    @PostMapping()
    public ItemDto addItem(@RequestHeader(headerName) long userId, @RequestBody @Valid ItemDto itemDto) {
        User user = userService.getUser(userId);
        Item item;
        if (itemDto.getRequestId()!=null){
            item = ItemMapper.toItem(itemDto,itemRequestService.findOneRequest(itemDto.getRequestId()));
        }else{
            item = ItemMapper.toItem(itemDto,null);
        }
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
        Item newItem = ItemMapper.toItem(itemDto, null);
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
            return itemService.findUserItems(userId,0,1).stream()
                    .filter(itemDtoToUser -> itemDtoToUser.getId() == itemId)
                    .findFirst().orElseThrow(() -> new ItemNotFoundException("Не найдено вещей у пользователя"));
        }
    }

    @GetMapping
    public List<ItemDtoToUser> findUserItems(@RequestHeader(headerName) long userId,
                                             @RequestParam(defaultValue = "0") @Min(0) int from,
                                             @RequestParam(defaultValue = "10") @Min(1) int size) {
        userService.getUser(userId);
        return itemService.findUserItems(userId,from/size,size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(headerName) long userId, @RequestParam String text,
                                     @RequestParam(defaultValue = "0") @Min(0) int from,
                                     @RequestParam(defaultValue = "10") @Min(1) int size) {
        userService.getUser(userId);
        return itemService.searchItems(text,from/size,size).stream()
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
