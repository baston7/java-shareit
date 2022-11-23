package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exeption.ItemRequestNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTests {
    private ItemRequestService service;
    @Mock
    ItemRequestRepository itemRequestRepository;

    @BeforeEach
    public void createService() {
        service = new ItemRequestService(itemRequestRepository);
    }

    @Test
    public void testGetWithoutRequest() {
        assertThrows(ItemRequestNotFoundException.class, () -> service.findOneRequest(22));
    }
}
