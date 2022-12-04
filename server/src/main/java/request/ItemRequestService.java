package request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import exeption.ItemRequestNotFoundException;
import request.model.ItemRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    public ItemRequest addRequest(ItemRequest itemRequest) {
        return itemRequestRepository.save(itemRequest);
    }


    public List<ItemRequest> findUserRequests(long userId) {
        return itemRequestRepository.findItemRequestByRequestorIdOrderByCreatedDesc(userId);
    }


    public List<ItemRequest> findAllRequests(long userId, int page, int size) {
        return itemRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(userId, PageRequest.of(page, size));
    }

    public ItemRequest findOneRequest(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Такого запроса не существует"));
    }
}
