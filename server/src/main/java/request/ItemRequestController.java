package request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import item.ItemService;
import item.model.Item;
import request.dto.ItemRequestDto;
import request.model.ItemRequest;
import user.UserService;
import user.model.User;

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
    private static final String HEADER_NAME = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(HEADER_NAME) long userId,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        User requestor = userService.getUser(userId);
        ItemRequest itemRequest = itemRequestService.addRequest(ItemRequestMapper.toItemRequest(itemRequestDto,
                requestor));
        return ItemRequestMapper.toItemRequestDto(itemRequest, Collections.emptyList());
    }

    @GetMapping
    public List<ItemRequestDto> findUserRequests(@RequestHeader(HEADER_NAME) long userId) {
        userService.getUser(userId);
        List<ItemRequest> requests = itemRequestService.findUserRequests(userId);

        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(request,
                        itemService.findItemsByRequest(request.getId())))
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequests(@RequestHeader(HEADER_NAME) long userId,
                                                @RequestParam int from,
                                                @RequestParam int size) {
        userService.getUser(userId);
        List<ItemRequest> requests = itemRequestService.findAllRequests(userId, from / size, size);
        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(request,
                        itemService.findItemsByRequest(request.getId())))
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findUserRequest(@RequestHeader(HEADER_NAME) long userId, @PathVariable long requestId) {
        userService.getUser(userId);
        List<Item> items = itemService.findItemsByRequest(requestId);
        return ItemRequestMapper.toItemRequestDto(itemRequestService.findOneRequest(requestId), items);
    }
}
