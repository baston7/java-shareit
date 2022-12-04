package request;

import lombok.experimental.UtilityClass;
import item.ItemMapper;
import item.dto.ItemDto;
import item.model.Item;
import request.dto.ItemRequestDto;
import request.model.ItemRequest;
import user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(requestor);
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        List<ItemDto> itemsDto = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getRequestor().getId(),
                itemRequest.getCreated(), itemsDto);
    }
}
