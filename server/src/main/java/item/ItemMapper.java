package item;

import lombok.experimental.UtilityClass;
import item.dto.CommentDto;
import item.dto.ItemDto;
import item.dto.ItemDtoToUser;
import item.model.Item;
import request.model.ItemRequest;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemDtoToUser toItemDtoToUser(Item item, List<CommentDto> comments) {
        return new ItemDtoToUser(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                comments
        );
    }

    public static Item toItem(ItemDto itemDto, ItemRequest itemRequest) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemRequest
        );
    }
}
