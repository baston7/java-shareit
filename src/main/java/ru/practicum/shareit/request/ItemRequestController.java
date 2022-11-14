package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final UserService userService;
    private final String headerName = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(headerName) long userId, @RequestBody @Valid ItemRequestDto itemRequestDto) {
        userService.getUser(userId);

    }

    @GetMapping
    public List<ItemRequestDto> findUserRequests(@RequestHeader(headerName) long userId) {
        return null;
        //получить список своих запросов вместе с данными об ответах на них. Для каждого запроса должны указываться описание,
        // дата и время создания и список ответов в формате: id вещи, название, id владельца.
        // Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequests(@RequestHeader(headerName) long userId, @RequestParam @Min(0) int from, @RequestParam @Positive int size) {
        return null;
        // получить список запросов, созданных другими пользователями.
        // С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить.
        // Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично.
        // Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения.
    }
    @GetMapping("/{requestId}")
    public ItemRequestDto findUserRequest(@RequestHeader(headerName) long userId, @PathVariable long requestId) {
        return null;
        // получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате,
        // что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
    }
}
