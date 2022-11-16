package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private final String headerName = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(headerName) long userId, @RequestBody @Valid ItemRequestDto itemRequestDto) {
        User requestor = userService.getUser(userId);
        ItemRequest itemRequest = itemRequestService.addRequest(ItemRequestMapper.toItemRequest(itemRequestDto, requestor));
        return ItemRequestMapper.toItemRequestDto(itemRequest, Collections.emptyList());
    }

    @GetMapping
    public List<ItemRequestDto> findUserRequests(@RequestHeader(headerName) long userId) {
        userService.getUser(userId);
        List<ItemRequest> requests = itemRequestService.findUserRequests(userId);

        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(request,
                        itemService.findItemsByRequest(request.getId())))
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequests(@RequestHeader(headerName) long userId,
                                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                                @RequestParam(defaultValue = "10") @Min(1) int size) {
        userService.getUser(userId);
        List<ItemRequest> requests = itemRequestService.findAllRequests(userId, from/size, size);
        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(request,
                        itemService.findItemsByRequest(request.getId())))
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findUserRequest(@RequestHeader(headerName) long userId, @PathVariable long requestId) {
        userService.getUser(userId);
        List<Item> items = itemService.findItemsByRequest(requestId);
        return ItemRequestMapper.toItemRequestDto(itemRequestService.findOneRequest(requestId), items);
    }
}
