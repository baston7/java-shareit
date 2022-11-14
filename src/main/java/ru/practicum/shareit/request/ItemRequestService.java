package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    public ItemRequest addRequest(long userId, ItemRequest itemRequest) {
        return itemRequestRepository.save(itemRequest);
    }


    public List<ItemRequest> findUserRequests(long userId) {
        //добавить валидацию
        return itemRequestRepository.findItemRequestByRequestor_IdOrderByCreatedDesc(userId);

        //получить список своих запросов вместе с данными об ответах на них. Для каждого запроса должны указываться описание,
        // дата и время создания и список ответов в формате: id вещи, название, id владельца.
        // Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
    }


    public List<ItemRequest> findAllRequests(long userId, int from, int size) {
        return null;
        // получить список запросов, созданных другими пользователями.
        // С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить.
        // Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично.
        // Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size — количество элементов для отображения.
    }

    public ItemRequest findUserRequest(long userId, long requestId) {
        return null;
        // получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате,
        // что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
    }
}
