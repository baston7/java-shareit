package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exeption.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTests {
    private ItemRequestService service;
    private ItemRequest request;
    private User user;
    @Mock
    ItemRequestRepository itemRequestRepository;

    @BeforeEach
    public void createService() {
        service = new ItemRequestService(itemRequestRepository);
        user = new User("Вася", "b@mail.ru");
        request = new ItemRequest(1, "Need car", user, LocalDateTime.now());
    }

    @Test
    public void testGetWithoutRequest() {
        assertThrows(ItemRequestNotFoundException.class, () -> service.findOneRequest(22));
    }

    @Test
    public void testAddRequest() {
        Mockito
                .when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(request);
        ItemRequest request1 = service.addRequest(request);
        assertEquals(1, request1.getId());
    }

    @Test
    public void testFindUserRequests() {
        Mockito
                .when(itemRequestRepository.findItemRequestByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(request));
        List<ItemRequest> requests = service.findUserRequests(1);
        assertEquals(1, requests.get(0).getId());
    }

    @Test
    public void testFindAllRequests() {
        Mockito
                .when(itemRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(anyLong(),
                        any(PageRequest.class)))
                .thenReturn(List.of(request));
        List<ItemRequest> requests = service.findAllRequests(1, 0, 10);
        assertEquals(1, requests.get(0).getId());
    }
}
