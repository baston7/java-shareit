package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    private UserService userService;
    User user = new User("Вася", "b@mail.ru");
    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void createService() {
        userService = new UserService(userRepository);
    }

    @Test
    public void testAddUser() {

        Mockito
                .when(userRepository.save(any(User.class)))
                .thenReturn(user);
        User user1 = userService.createUser(new User("Вася", "b@mail.ru"));
        assertEquals(user.getName(), user1.getName());
    }

    @Test
    public void testGetWithoutUser() {
        assertThrows(UserNotFoundException.class, () -> userService.getUser(6));
    }

    @Test
    public void testGetCreatedUser() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        User user1 = userService.getUser(1);
        assertEquals(user.getName(), user1.getName());
    }
}
